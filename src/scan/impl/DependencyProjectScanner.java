package scan.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import core.model.data.Edge;
import core.model.data.Graph;


import core.model.data.Module;
import core.model.data.Node;
import scan.ProjectLanguage;
import scan.ProjectScanner;
import scan.SourceCodeFile;
import scan.utils.FileUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class DependencyProjectScanner implements ProjectScanner {

    private final String dependencyJarPath;

    public DependencyProjectScanner(String dependencyJarPath) {
        this.dependencyJarPath = dependencyJarPath;
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    @Override
    public Graph scanProject(String projectPath, ProjectLanguage projectLanguage, boolean needScan) throws Exception {
        // 当前仅支持java语言
        if (projectLanguage != ProjectLanguage.JAVA) {
            throw new RuntimeException("Language not supported");
        }

        // 构造命令执行依赖分析
        String currentDirectory = System.getProperty("user.dir");
        if (needScan) {
            String command = String.format("java -jar %s -d=%s java %s temp",
                    dependencyJarPath, currentDirectory, projectPath);
            System.out.println(command);
            Process process = Runtime.getRuntime()
                    .exec(command);
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            ExecutorService excutorService = Executors.newSingleThreadExecutor();
            excutorService.submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
            excutorService.shutdown();
        }

        // 解析依赖分析文件
        String rawJson = FileUtils.readToString(currentDirectory + "/temp.json");
        if (rawJson == null) {
            throw new RuntimeException("Dependency tool execute failed");
        }
        JSONObject resultObject = JSONObject.parseObject(rawJson);

        //构造代表源文件的节点
        List<String> sourceCodeFiles = JSON.parseArray(resultObject.getJSONArray("variables").toJSONString(),
                String.class);
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < sourceCodeFiles.size(); i++) {
            String absolutePath = sourceCodeFiles.get(i);
            String fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
            String fileProjectPath = absolutePath.substring(projectPath.length());
            nodeList.add(new Node(i, fileName, new SourceCodeFile(absolutePath, fileProjectPath, fileName)));
        }

        // 根据文件目录结构构造初始模块
        Set<Module> initModules = new HashSet<>();
        Map<String, Set<Node>> dirs = new HashMap<>();
        for (Node node : nodeList) {
            Set<Node> nodeSet = dirs.computeIfAbsent(node.getReferenceObject().getProjectDir(), k -> new HashSet<>());
            nodeSet.add(node);
        }
        for (String projectDir : dirs.keySet()) {
            initModules.add(new Module(projectDir, dirs.get(projectDir), true));
        }

        // 构建代表依赖关系的边
        /*
         *Depends* supports major dependency types, including:
         * Call: function/method invoke                                                                     3
         * Cast: type cast                                                                                  1
         * Contain: variable/field definition                                                               2
         * Create: create an instance of a certain type                                                     3
         * Extend: parent-child relation                                                                    15
         * Implement: implemented interface                                                                 15
         * Import/Include: for example, java ```import```, c/c++ ```#include```, ruby ```require```.        0
         * Mixin: mix-in relation, for example ruby include                                                 0
         * Parameter: as a parameter of a method                                                            2
         * Return: returned type                                                                            4
         * Throw: throw exceptions                                                                          4
         * Use: use or set variables                                                                        2
         * ImplLink: the implementation link between call and the implementation of prototype.              10
         */
        Set<Edge> edgeSet = new HashSet<>();
        JSONArray cellsArray = resultObject.getJSONArray("cells");
        for (int i = 0; i < cellsArray.size(); i++) {
            JSONObject currentCell = cellsArray.getJSONObject(i);
            int srcIndex = currentCell.getInteger("src");
            int destIndex = currentCell.getInteger("dest");

            JSONObject valueObject = currentCell.getJSONObject("values");
            double edgeWeight = 0;
            if (valueObject.containsKey("Call")) {
                double callCount = valueObject.getDouble("Call");
                edgeWeight += callCount * 3;
            }
            if (valueObject.containsKey("Cast")) {
                double callCount = valueObject.getDouble("Cast");
                edgeWeight += callCount * 1;
            }
            if (valueObject.containsKey("Contain")) {
                double callCount = valueObject.getDouble("Contain");
                edgeWeight += callCount * 2;
            }
            if (valueObject.containsKey("Create")) {
                double callCount = valueObject.getDouble("Create");
                edgeWeight += callCount * 3;
            }
            if (valueObject.containsKey("Extend")) {
                double callCount = valueObject.getDouble("Extend");
                edgeWeight += callCount * 15;
            }
            if (valueObject.containsKey("Implement")) {
                double callCount = valueObject.getDouble("Implement");
                edgeWeight += callCount * 15;
            }
            if (valueObject.containsKey("Parameter")) {
                double callCount = valueObject.getDouble("Parameter");
                edgeWeight += callCount * 2;
            }
            if (valueObject.containsKey("Parameter")) {
                double callCount = valueObject.getDouble("Parameter");
                edgeWeight += callCount * 2;
            }
            if (valueObject.containsKey("Return")) {
                double callCount = valueObject.getDouble("Return");
                edgeWeight += callCount * 4;
            }
            if (valueObject.containsKey("Throw")) {
                double callCount = valueObject.getDouble("Throw");
                edgeWeight += callCount * 4;
            }
            if (valueObject.containsKey("Use")) {
                double callCount = valueObject.getDouble("Use");
                edgeWeight += callCount * 2;
            }
            if (valueObject.containsKey("ImplLink")) {
                double callCount = valueObject.getDouble("ImplLink");
                edgeWeight += callCount * 10;
            }
            Edge newEdge = new Edge(nodeList.get(srcIndex), nodeList.get(destIndex), edgeWeight);
            edgeSet.add(newEdge);
        }

        return new Graph(new HashSet<>(nodeList), edgeSet, initModules);
    }
}


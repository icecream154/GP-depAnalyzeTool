package cli;

import com.alibaba.fastjson.JSON;
import core.analyze.*;
import core.analyze.config.AnalyzeClusterConfig;
import core.analyze.config.AnalyzeMatchConfig;
import core.analyze.config.AnalyzerIterationStrategy;
import core.analyze.config.ProjectConfig;
import core.analyze.output.ProjectJsonOutput;
import core.model.data.Graph;
import scan.ProjectLanguage;
import scan.ProjectScanner;
import scan.impl.DependencyProjectScanner;
import scan.match.ProjectDependencySpecification;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;

public class CommandLineInterface {

    public static final String defaultDependencyJarPath =
            "/Users/panxingyu/Desktop/GraduationProject/codes/scanner/depends-0.9.6/depends.jar";

    public static void main(String[] args) throws Exception {
        if (args.length <= 1) {
            System.out.println("Arguments not enougth, please check the README file.");
            return;
        }

        String taskType = "";
        if (args[0].equals("-c")) {
            taskType = "cluster";
        } else if (args[0].equals("-m")) {
            taskType = "match";
        } else {
            System.out.println("Arguments not correct, please check the README file.");
            return;
        }

        // 命令行参数
        String projectLanguage = "Java";     // 编程语言默认为Java，暂不支持其他语言
        String projectFrame = "";            // 编程框架默认为空，暂不支持
        String looseReClusterFlag = "false"; // 再聚类配置项，默认FALSE

        // 余下的参数是待分析的项目路径
        String[] projectPathList = Arrays.copyOfRange(args, 1, args.length);
        for (String s : projectPathList) {
            String specificationYamlFile = s + "/project-structure.yaml";   // yaml文件路径，暂不支持配置
            CommandLineInterface.executeAnalyzeTask(s, projectLanguage, projectFrame,
                    taskType, specificationYamlFile, looseReClusterFlag);
        }
    }

    private static void executeAnalyzeTask(String projectPath, String projectLanguage, String projectFrame,
                                           String taskType, String specificationYamlFile,
                                           String looseReClusterFlag) throws Exception {
        // 读取yaml文件
        ProjectDependencySpecification projectDependencySpecification =
                ProjectDependencySpecification.load(specificationYamlFile);

        // 导入项目，生成依赖图
        String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1);
        ProjectScanner projectScanner = new DependencyProjectScanner(CommandLineInterface.defaultDependencyJarPath);
        Graph initGraph = projectScanner.scanProject(projectName, projectPath, ProjectLanguage.JAVA,
                true, projectDependencySpecification);

        // 配置分析器
        Analyzer analyzer = null;
        AnalyzeResult analyzeResult = null;
        if (taskType.equals("cluster")) {
            ProjectConfig projectConfig = new ProjectConfig(projectName, projectLanguage, projectFrame);
            analyzer = new Analyzer(initGraph, AnalyzeTaskType.CLUSTER, projectConfig, null,
                    new AnalyzeClusterConfig(AnalyzerIterationStrategy.FIX, 1, looseReClusterFlag.equals("true")));
        } else if (taskType.equals("match")) {
            ProjectConfig projectConfig = new ProjectConfig(projectDependencySpecification.getProjectName(),
                    projectDependencySpecification.getProjectLanguage(), projectDependencySpecification.getProjectFrame());
            analyzer = new Analyzer(initGraph, AnalyzeTaskType.MATCH, projectConfig, new AnalyzeMatchConfig(
                    projectDependencySpecification.getModuleAllowedDependencyFromGraph(initGraph)
            ), null);
        }

        System.out.println("Start Analyze");
        analyzeResult = analyzer.executeAnalyze();
        // analyzeResult.showIterationInfo();
        ProjectJsonOutput output = analyzeResult.buildJsonOutput();

        // 将结果写入文件
        String outputFile = System.getProperty("user.dir") + "/output/" + projectName +
                "-" + taskType + ".json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(JSON.toJSONString(output, true));
        writer.close();

        System.out.println(outputFile + " write done");
    }
}

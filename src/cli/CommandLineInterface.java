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

public class CommandLineInterface {

    public static final String defaultDependencyJarPath =
            "/Users/panxingyu/Desktop/GraduationProject/codes/scanner/depends-0.9.6/depends.jar";

    public static void main(String[] args) throws Exception {
        // 命令行参数
        String projectPath = "";
        String projectLanguage = "";
        String projectFrame = "";
        String taskType = "";
        String specificationYamlFile = "";
        String looseReClusterFlag = "";

        // 参数初始化
        // projectPath = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/smallProjects/CourseTakeReportTool";
        // projectPath = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/webServerProjects/springboot-demo/complete";

        projectLanguage = "Java";
        projectFrame = "";
        taskType = "cluster";
        looseReClusterFlag = "false";
        // specificationYamlFile = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/smallProjects/CourseTakeReportTool/src/project-structure.yaml";
        String[] projectPathList = new String[]{
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/tomcat",
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/spring-boot",
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/kafka",
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/rocketmq",
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/iotdb",
//                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/hive",
                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/hbase",
                "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/largeProjects/cassandra"
        };

        for (String s : projectPathList) {
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
                "-" + taskType + "-loose-" + looseReClusterFlag + ".json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(JSON.toJSONString(output, true));
        writer.close();

        System.out.println(outputFile + " write done");
    }
}

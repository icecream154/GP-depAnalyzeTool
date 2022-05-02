package cli;

import core.analyze.*;
import core.analyze.config.AnalyzeClusterConfig;
import core.analyze.config.AnalyzeMatchConfig;
import core.analyze.config.AnalyzerIterationStrategy;
import core.model.data.Graph;
import scan.ProjectLanguage;
import scan.ProjectScanner;
import scan.impl.DependencyProjectScanner;
import scan.match.ProjectDependencySpecification;

public class CommandLineInterface {

    public static final String defaultDependencyJarPath =
            "/Users/panxingyu/Desktop/GraduationProject/codes/scanner/depends-0.9.6/depends.jar";

    public static void main(String[] args) throws Exception {
        // 命令行参数
        String projectPath = "";
        projectPath = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/smallProjects/CourseTakeReportTool";
        //projectPath = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/smallProjects/ZipToolWithHuffmanCoding";
        String taskType = "match";
        String specificationYamlFile = "/Users/panxingyu/Desktop/GraduationProject/codes/sampleProjects/smallProjects/CourseTakeReportTool/src/project-structure.yaml";

        // 读取yaml文件
        ProjectDependencySpecification projectDependencySpecification =
                ProjectDependencySpecification.load(specificationYamlFile);

        // 导入项目，生成依赖图
        ProjectScanner projectScanner = new DependencyProjectScanner(CommandLineInterface.defaultDependencyJarPath);
        Graph initGraph = projectScanner.scanProject(projectPath, ProjectLanguage.JAVA,
                false, projectDependencySpecification);

        // 配置分析器
        Analyzer analyzer = null;
        AnalyzeResult analyzeResult = null;
        if (taskType.equals("cluster")) {
            analyzer = new Analyzer(initGraph, AnalyzeTaskType.CLUSTER, null,
                    new AnalyzeClusterConfig(AnalyzerIterationStrategy.FIX, 1));
        } else if (taskType.equals("match")) {
            analyzer = new Analyzer(initGraph, AnalyzeTaskType.MATCH, new AnalyzeMatchConfig(
                    projectDependencySpecification.getModuleAllowedDependencyFromGraph(initGraph)
            ), null);
        }

        analyzeResult = analyzer.executeAnalyze();
        analyzeResult.showIterationInfo();
    }
}

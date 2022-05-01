package cli;

import core.analyze.AnalyzeConfig;
import core.analyze.AnalyzeResult;
import core.analyze.Analyzer;
import core.analyze.AnalyzerIterationStrategy;
import core.model.data.Graph;
import scan.ProjectLanguage;
import scan.ProjectScanner;
import scan.impl.DependencyProjectScanner;

public class CommandLineInterface {
    public static void main(String[] args) {
        try {
            ProjectScanner projectScanner = new DependencyProjectScanner(
                    "/Users/panxingyu/Desktop/depends-0.9.6/depends.jar");
            String projectPath = "";
            projectPath = "/Users/panxingyu/Desktop/cassandra";
//            projectPath = "/Users/panxingyu/Desktop/depAnalyzeTool";
//            projectPath = "/Users/panxingyu/Documents/Fudan_4A/OOAD/lab2/java_lab2";
            Graph initGraph = projectScanner.scanProject(projectPath, ProjectLanguage.JAVA, false);
            System.out.println("Scan over!");

            Analyzer analyzer = new Analyzer(initGraph, new AnalyzeConfig(AnalyzerIterationStrategy.FIX, 3));
            AnalyzeResult result = analyzer.executeAnalyze();
            result.showIterationInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

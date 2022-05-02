package core.analyze;

import core.analyze.iteration.IterationAnalyzeResult;
import core.model.data.Graph;

import java.util.List;

public class AnalyzeResult {
    private final AnalyzeTaskType analyzeTaskType;
    private final Graph resultGraph;
    private final int iterationTime;
    private final List<IterationAnalyzeResult> iterationAnalyzeResultList;

    public AnalyzeResult(AnalyzeTaskType analyzeTaskType, Graph resultGraph,
                         int iterationTime, List<IterationAnalyzeResult> iterationAnalyzeResultList) {
        this.resultGraph = resultGraph;
        this.analyzeTaskType = analyzeTaskType;
        this.iterationTime = iterationTime;
        this.iterationAnalyzeResultList = iterationAnalyzeResultList;
    }

    public Graph getResultGraph() {
        return resultGraph;
    }

    public AnalyzeTaskType getAnalyzeTaskType() {
        return analyzeTaskType;
    }

    public int getIterationTime() {
        return iterationTime;
    }

    public List<IterationAnalyzeResult> getIterationAnalyzeResultList() {
        return iterationAnalyzeResultList;
    }

    @Override
    public String toString() {
        return "AnalyzeResult{\n" +
                "analyzeTaskType=" + analyzeTaskType + ",\n" +
                "resultGraph=" + resultGraph + ",\n" +
                "iterationTime=" + iterationTime + ",\n" +
                "iterationAnalyzeResultList=" + iterationAnalyzeResultList + "\n" +
                '}';
    }

    public void showIterationInfo() {
        System.out.println("Analyze Task: [" + analyzeTaskType + "]");
        for (IterationAnalyzeResult iterationAnalyzeResult : iterationAnalyzeResultList) {
            System.out.println(iterationAnalyzeResult.getIterationAnalyzeResultInfo());
        }
    }
}

package core.analyze;

import core.analyze.iteration.IterationAnalyzeResult;
import core.model.data.Graph;

import java.util.List;

public class AnalyzeResult {
    private Graph resultGraph;
    private int iterationTime;
    private List<IterationAnalyzeResult> iterationAnalyzeResultList;

    public AnalyzeResult(Graph resultGraph, int iterationTime, List<IterationAnalyzeResult> iterationAnalyzeResultList) {
        this.resultGraph = resultGraph;
        this.iterationTime = iterationTime;
        this.iterationAnalyzeResultList = iterationAnalyzeResultList;
    }

    public Graph getResultGraph() {
        return resultGraph;
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
                "resultGraph=" + resultGraph + ",\n" +
                "iterationTime=" + iterationTime + ",\n" +
                "iterationAnalyzeResultList=" + iterationAnalyzeResultList + "\n" +
                '}';
    }

    public void showIterationInfo() {
        for (IterationAnalyzeResult iterationAnalyzeResult : iterationAnalyzeResultList) {
            System.out.println(iterationAnalyzeResult.getIterationAnalyzeResultInfo());
        }
    }
}

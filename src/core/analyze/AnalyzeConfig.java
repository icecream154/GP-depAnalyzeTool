package core.analyze;

public class AnalyzeConfig {


    private AnalyzerIterationStrategy iterationStrategy;
    private int fixIteration;

    public AnalyzeConfig(AnalyzerIterationStrategy iterationStrategy, int fixIteration) {
        this.iterationStrategy = iterationStrategy;
        this.fixIteration = fixIteration;
    }

    public AnalyzerIterationStrategy getIterationStrategy() {
        return iterationStrategy;
    }

    public void setIterationStrategy(AnalyzerIterationStrategy iterationStrategy) {
        this.iterationStrategy = iterationStrategy;
    }

    public int getFixIteration() {
        return fixIteration;
    }

    public void setFixIteration(int fixIteration) {
        this.fixIteration = fixIteration;
    }
}

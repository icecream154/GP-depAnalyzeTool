package core.analyze.config;

public class AnalyzeClusterConfig {

    private final AnalyzerIterationStrategy iterationStrategy;
    private final int fixIteration;

    public AnalyzeClusterConfig(AnalyzerIterationStrategy iterationStrategy, int fixIteration) {
        this.iterationStrategy = iterationStrategy;
        this.fixIteration = fixIteration;
    }

    public AnalyzerIterationStrategy getIterationStrategy() {
        return iterationStrategy;
    }

    public int getFixIteration() {
        return fixIteration;
    }
}

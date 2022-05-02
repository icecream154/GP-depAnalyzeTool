package core.analyze.config;

public class AnalyzeClusterConfig {

    private final AnalyzerIterationStrategy iterationStrategy;
    private final int fixIteration;
    private final boolean looseReCluster;

    public AnalyzeClusterConfig(AnalyzerIterationStrategy iterationStrategy, int fixIteration, boolean looseReCluster) {
        this.iterationStrategy = iterationStrategy;
        this.fixIteration = fixIteration;
        this.looseReCluster = looseReCluster;
    }

    public AnalyzerIterationStrategy getIterationStrategy() {
        return iterationStrategy;
    }

    public int getFixIteration() {
        return fixIteration;
    }

    public boolean isLooseReCluster() {
        return looseReCluster;
    }
}

package scan.match.test;

import core.analyze.AnalyzeResult;
import core.analyze.AnalyzeTaskType;
import core.analyze.Analyzer;
import core.analyze.config.AnalyzeClusterConfig;
import core.analyze.config.AnalyzerIterationStrategy;
import core.model.data.Edge;
import core.model.data.Graph;
import core.model.data.Module;
import core.model.data.Node;
import scan.match.ProjectDependencySpecification;

import java.util.*;

public class ProjectDependencySpecificationLoadTest {
    @org.junit.jupiter.api.Test
    void testLoadDependencySpecification() {
        ProjectDependencySpecification specification =
                ProjectDependencySpecification.load("./src/scan/match/test/depend-structure.yaml");
        System.out.println(specification);
        assert (specification != null);
    }
}

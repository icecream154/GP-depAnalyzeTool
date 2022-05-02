package core.analyze.config;

import java.util.Map;
import java.util.Set;
import core.model.data.Module;

public class AnalyzeMatchConfig {

    private final Map<Module, Set<Module>> allowedDependentModules;

    public AnalyzeMatchConfig(Map<Module, Set<Module>> allowedDependentModules) {
        this.allowedDependentModules = allowedDependentModules;
    }

    public Map<Module, Set<Module>> getAllowedDependentModules() {
        return allowedDependentModules;
    }
}

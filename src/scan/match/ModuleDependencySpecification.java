package scan.match;

import java.util.List;

public class ModuleDependencySpecification {
    private String moduleName;
    private String moduleDescription;
    private String modulePath;
    private List<String> dependentModules;
    private List<String> dependentModulePaths;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDescription() {
        return moduleDescription;
    }

    public void setModuleDescription(String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public List<String> getDependentModules() {
        return dependentModules;
    }

    public void setDependentModules(List<String> dependentModules) {
        this.dependentModules = dependentModules;
    }

    public List<String> getDependentModulePaths() {
        return dependentModulePaths;
    }

    public void setDependentModulePaths(List<String> dependentModulePaths) {
        this.dependentModulePaths = dependentModulePaths;
    }

    @Override
    public String toString() {
        return "ModuleDependencySpecification{" +
                "moduleName='" + moduleName + '\'' +
                ", moduleDescription='" + moduleDescription + '\'' +
                ", modulePath='" + modulePath + '\'' +
                ", dependentModules=" + dependentModules +
                ", dependentModulePaths=" + dependentModulePaths +
                '}';
    }
}

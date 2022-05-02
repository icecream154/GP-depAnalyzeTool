package scan.match;

import core.model.data.Graph;
import core.model.data.Module;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class ProjectDependencySpecification {
    private String projectName;
    private String projectLanguage;
    private String projectFrame;
    private List<ModuleDependencySpecification> projectStructure;

    @Override
    public String toString() {
        return "ProjectDependencySpecification{" +
                "projectName='" + projectName + '\'' +
                ", projectLanguage='" + projectLanguage + '\'' +
                ", projectFrame='" + projectFrame + '\'' +
                ", projectStructure=" + projectStructure +
                '}';
    }

    public static ProjectDependencySpecification load(String configFilePath) {
        Yaml yaml = new Yaml();
        try {
            ProjectDependencySpecification specification =
                    yaml.loadAs(new FileInputStream(configFilePath), ProjectDependencySpecification.class);
            for (ModuleDependencySpecification moduleDependencySpecification : specification.getProjectStructure()) {
                if (moduleDependencySpecification.getDependentModules() == null) {
                    moduleDependencySpecification.setDependentModules(new ArrayList<>());
                }
                List<String> dependentModulePaths = new ArrayList<>();
                for (String moduleName : moduleDependencySpecification.getDependentModules()) {
                    for (ModuleDependencySpecification iter : specification.getProjectStructure()) {
                        if (moduleName.equals(iter.getModuleName())) {
                            dependentModulePaths.add(iter.getModulePath());
                            break;
                        }
                    }
                }
                moduleDependencySpecification.setDependentModulePaths(dependentModulePaths);
            }
            return specification;
        } catch (FileNotFoundException ex) {
            System.out.println("Yaml Config not found");
        }
        return null;
    }

    public Map<Module, Set<Module>> getModuleAllowedDependencyFromGraph(Graph graph) {
        Map<Module, Set<Module>> allowedDependentModules = new HashMap<>();
        for (ModuleDependencySpecification iter : projectStructure) {
            // System.out.println("Fetch Task: " + iter.getModuleName());
            Module iterModule = graph.getModuleByName(iter.getModuleName());
            // System.out.println("\tIter Module: " + iterModule.getName());
            Set<Module> iterAllowedDependentModules = new HashSet<>();
            for (String allowedModulesName : iter.getDependentModules()) {
                Module allowedModule = graph.getModuleByName(allowedModulesName);
                // System.out.println("\tallowed: " + allowedModule.getName());
                iterAllowedDependentModules.add(allowedModule);
            }
            allowedDependentModules.put(iterModule, iterAllowedDependentModules);
        }
        // System.out.println(allowedDependentModules.keySet());
        return allowedDependentModules;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLanguage() {
        return projectLanguage;
    }

    public void setProjectLanguage(String projectLanguage) {
        this.projectLanguage = projectLanguage;
    }

    public String getProjectFrame() {
        return projectFrame;
    }

    public void setProjectFrame(String projectFrame) {
        this.projectFrame = projectFrame;
    }

    public List<ModuleDependencySpecification> getProjectStructure() {
        return projectStructure;
    }

    public void setProjectStructure(List<ModuleDependencySpecification> projectStructure) {
        this.projectStructure = projectStructure;
    }
}

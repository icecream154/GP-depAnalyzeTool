package scan;

import core.model.data.Graph;
import scan.match.ProjectDependencySpecification;

public interface ProjectScanner {
    Graph scanProject(String projectName, String projectPath, ProjectLanguage projectLanguage,
                      boolean needScan, ProjectDependencySpecification projectDependencySpecification) throws Exception;
}

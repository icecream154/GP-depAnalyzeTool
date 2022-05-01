package scan;

import core.model.data.Graph;

public interface ProjectScanner {
    Graph scanProject(String projectPath, ProjectLanguage projectLanguage, boolean needScan) throws Exception;
}

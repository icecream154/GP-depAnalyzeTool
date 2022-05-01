package scan;

import core.model.data.NodeReferenceObject;

public class SourceCodeFile implements NodeReferenceObject {
    private final String absolutePath;
    private final String projectPath;
    private final String fileName;

    public SourceCodeFile(String absolutePath, String projectPath, String fileName) {
        this.absolutePath = absolutePath;
        this.projectPath = projectPath;
        this.fileName = fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String getAbsoluteDir() {
        return absolutePath.substring(0, absolutePath.length() - fileName.length());
    }

    public String getProjectPath() {
        return projectPath;
    }

    @Override
    public String getProjectDir() {
        return projectPath.substring(0, projectPath.length() - fileName.length());
    }

    public String getFileName() {
        return fileName;
    }
}

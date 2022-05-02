package core.analyze.config;

public class ProjectConfig {
    private final String projectName;
    private final String projectLanguage;
    private final String projectFrame;

    public ProjectConfig(String projectName, String projectLanguage, String projectFrame) {
        this.projectName = projectName;
        this.projectLanguage = projectLanguage;
        this.projectFrame = projectFrame;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectLanguage() {
        return projectLanguage;
    }

    public String getProjectFrame() {
        return projectFrame;
    }
}

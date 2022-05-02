package core.analyze.output;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Arrays;

public class ProjectJsonOutput {
    @JSONField(name = "projectName", ordinal = 1)
    private final String projectName;

    @JSONField(name = "projectLanguage", ordinal = 2)
    private final String projectLanguage;

    @JSONField(name = "projectFrame", ordinal = 3)
    private final String projectFrame;

    @JSONField(name = "taskType", ordinal = 4)
    private final String taskType;

    @JSONField(name = "statics", ordinal = 5)
    private final ProjectStatics statics;

    @JSONField(name = "modules", ordinal = 6)
    private final ModuleJsonOutput[] modules;

    public ProjectJsonOutput(String projectName, String projectLanguage, String projectFrame,
                             String taskType, ProjectStatics statics, ModuleJsonOutput[] modules) {
        this.projectName = projectName;
        this.projectLanguage = projectLanguage;
        this.projectFrame = projectFrame;
        this.taskType = taskType;
        this.statics = statics;
        this.modules = modules;
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

    public String getTaskType() {
        return taskType;
    }

    public ProjectStatics getStatics() {
        return statics;
    }

    public ModuleJsonOutput[] getModules() {
        return modules;
    }

    @Override
    public String toString() {
        return "ProjectJsonOutput{" +
                "projectName='" + projectName + '\'' +
                ", projectLanguage='" + projectLanguage + '\'' +
                ", projectFrame='" + projectFrame + '\'' +
                ", taskType='" + taskType + '\'' +
                ", statics='" + statics + '\'' +
                ", modules=" + Arrays.toString(modules) +
                '}';
    }
}

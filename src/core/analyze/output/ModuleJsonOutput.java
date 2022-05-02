package core.analyze.output;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

public class ModuleJsonOutput {

    @JSONField(name = "moduleName", ordinal = 1)
    private String moduleName;

    @JSONField(name = "modulePath", ordinal = 2)
    private String modulePath;

    @JSONField(name = "moduleNodeNames", ordinal = 3)
    private String[] moduleNodeNames;

    @JSONField(name = "dependentModuleNames", ordinal = 4)
    private String[] dependentModuleNames;

    @JSONField(name = "accuray", ordinal = 5)
    private String accuray;

    @JSONField(name = "unMatchNodeNames", ordinal = 6)
    private String[] unMatchNodeNames;

    @JSONField(name = "matchNodeNames", ordinal = 7)
    private String[] matchNodeNames;

    @JSONField(name = "moduleNodeAdvises", ordinal = 8)
    private ModuleNodeAdvise[] moduleNodeAdvises;

    public ModuleJsonOutput(String moduleName, String modulePath, String[] moduleNodeNames,
                            String[] dependentModuleNames, String accuray, String[] unMatchNodeNames,
                            String[] matchNodeNames, ModuleNodeAdvise[] moduleNodeAdvises) {
        this.moduleName = moduleName;
        this.modulePath = modulePath;
        this.moduleNodeNames = moduleNodeNames;
        this.dependentModuleNames = dependentModuleNames;
        this.accuray = accuray;
        this.unMatchNodeNames = unMatchNodeNames;
        this.matchNodeNames = matchNodeNames;
        this.moduleNodeAdvises = moduleNodeAdvises;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public String[] getModuleNodeNames() {
        return moduleNodeNames;
    }

    public void setModuleNodeNames(String[] moduleNodeNames) {
        this.moduleNodeNames = moduleNodeNames;
    }

    public String[] getDependentModuleNames() {
        return dependentModuleNames;
    }

    public void setDependentModuleNames(String[] dependentModuleNames) {
        this.dependentModuleNames = dependentModuleNames;
    }

    public String getAccuray() {
        return accuray;
    }

    public void setAccuray(String accuray) {
        this.accuray = accuray;
    }

    public String[] getUnMatchNodeNames() {
        return unMatchNodeNames;
    }

    public void setUnMatchNodeNames(String[] unMatchNodeNames) {
        this.unMatchNodeNames = unMatchNodeNames;
    }

    public String[] getMatchNodeNames() {
        return matchNodeNames;
    }

    public void setMatchNodeNames(String[] matchNodeNames) {
        this.matchNodeNames = matchNodeNames;
    }

    public ModuleNodeAdvise[] getModuleNodeAdvises() {
        return moduleNodeAdvises;
    }

    public void setModuleNodeAdvises(ModuleNodeAdvise[] moduleNodeAdvises) {
        this.moduleNodeAdvises = moduleNodeAdvises;
    }

    @Override
    public String toString() {
        return "ModuleJsonOutput{" +
                "moduleName='" + moduleName + '\'' +
                ", modulePath='" + modulePath + '\'' +
                ", moduleNodeNames=" + Arrays.toString(moduleNodeNames) +
                ", dependentModuleNames=" + Arrays.toString(dependentModuleNames) +
                ", accuray='" + accuray + '\'' +
                ", unMatchNodeNames=" + Arrays.toString(unMatchNodeNames) +
                ", matchNodeNames=" + Arrays.toString(matchNodeNames) +
                ", moduleNodeAdvises=" + Arrays.toString(moduleNodeAdvises) +
                '}';
    }
}

package core.analyze.output;

import com.alibaba.fastjson.annotation.JSONField;

public class ModuleNodeAdvise {

    @JSONField(name = "nodeName", ordinal = 1)
    private String nodeName;

    @JSONField(name = "dependentModule", ordinal = 2)
    private String dependentModule;

    public ModuleNodeAdvise(String nodeName, String dependentModule) {
        this.nodeName = nodeName;
        this.dependentModule = dependentModule;
    }

    @Override
    public String toString() {
        return "ModuleNodeAdvise{" +
                "nodeName='" + nodeName + '\'' +
                ", dependentModule='" + dependentModule + '\'' +
                '}';
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getDependentModule() {
        return dependentModule;
    }

    public void setDependentModule(String dependentModule) {
        this.dependentModule = dependentModule;
    }
}

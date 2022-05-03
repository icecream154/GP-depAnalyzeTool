package core.analyze.output;

import com.alibaba.fastjson.annotation.JSONField;

public class ProjectStatics {
    // 模块数量相关
    @JSONField(name = "totalModules", ordinal = 1)
    public int totalModules = 0;
    @JSONField(name = "originalModules", ordinal = 2)
    public int originalModules = 0;
    @JSONField(name = "newModules", ordinal = 3)
    public int newModules = 0;
    // 准确程度相关
    @JSONField(name = "accurateSpecifiations", ordinal = 4)
    public int accurateSpecifiations = 0;
    @JSONField(name = "accurateAverageAccuray", ordinal = 5)
    public double accurateAverageAccuray = 0;
    @JSONField(name = "normalSpecifiations", ordinal = 6)
    public int normalSpecifiations = 0;
    @JSONField(name = "normalAverageAccuray", ordinal = 7)
    public double normalAverageAccuray = 0;
    @JSONField(name = "looseSpecifications", ordinal = 8)
    public int looseSpecifications = 0;
    @JSONField(name = "looseAverageAccuray", ordinal = 9)
    public double looseAverageAccuray = 0;
    @JSONField(name = "averageAccuray", ordinal = 10)
    public double averageAccuray = 0;
    // 节点相关
    @JSONField(name = "totalNodes", ordinal = 11)
    public int totalNodes = 0;
    @JSONField(name = "accurateNodes", ordinal = 12)
    public int accurateNodes = 0;
    @JSONField(name = "normalNodes", ordinal = 13)
    public int normalNodes = 0;
    @JSONField(name = "looseNodes", ordinal = 14)
    public int looseNodes = 0;
    @JSONField(name = "abnormalNodes", ordinal = 15)
    public int abnormalNodes = 0;
    @JSONField(name = "newModuleNodes", ordinal = 16)
    public int newModuleNodes = 0;
    // 比例相关
    @JSONField(name = "accurateSpecifiationRate", ordinal = 17)
    public double accurateSpecifiationRate = 0;
    @JSONField(name = "normalSpecifiationRate", ordinal = 18)
    public double normalSpecifiationRate = 0;
    @JSONField(name = "looseSpecifiationRate", ordinal = 19)
    public double looseSpecifiationRate = 0;
    @JSONField(name = "accurateNodeRate", ordinal = 20)
    public double accurateNodeRate = 0;
    @JSONField(name = "normalNodeRate", ordinal = 21)
    public double normalNodeRate = 0;
    @JSONField(name = "looseNodeRate", ordinal = 22)
    public double looseNodeRate = 0;
    // 建议相关
    @JSONField(name = "advises", ordinal = 23)
    public int advises = 0;

    public void calculateRate() {
        int specificationSum = accurateSpecifiations + normalSpecifiations + looseSpecifications;
        if (specificationSum != 0) {
            accurateSpecifiationRate = (double) accurateSpecifiations / specificationSum;
            normalSpecifiationRate = (double) normalSpecifiations / specificationSum;
            looseSpecifiationRate = (double) looseSpecifications / specificationSum;
        }

        int nodeSum = accurateNodes + normalNodes + looseNodes;
        if (specificationSum != 0) {
            accurateNodeRate = (double) accurateNodes / nodeSum;
            normalNodeRate = (double) normalNodes / nodeSum;
            looseNodeRate = (double) looseNodes / nodeSum;
        }
    }
}

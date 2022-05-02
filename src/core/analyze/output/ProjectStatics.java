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
}

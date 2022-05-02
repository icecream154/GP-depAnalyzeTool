package core.model.specification;

import core.model.data.Module;
import core.model.data.Node;

/**
 * 表示某一节点对对模块特征离散程度的贡献
 */
public class ModuleSpecificationAccurayContribution {
    private final Module dependentModule;
    private final Node maxDependentNode;
    private final double maxL2NormalizedWeight;
    private final Node minDependentNode;
    private final double minL2NormalizedWeight;
    private final double averageL2NormalizedWeight;
    private final double accuracyContributionWeight;
    private double accuracyContributionRate;

    public ModuleSpecificationAccurayContribution(Module dependentModule, Node maxDependentNode,
                                                  double maxL2NormalizedWeight, Node minDependentNode,
                                                  double minL2NormalizedWeight, double averageL2NormalizedWeight) {
        this.dependentModule = dependentModule;
        this.maxDependentNode = maxDependentNode;
        this.maxL2NormalizedWeight = maxL2NormalizedWeight;
        this.minDependentNode = minDependentNode;
        this.minL2NormalizedWeight = minL2NormalizedWeight;
        this.averageL2NormalizedWeight = averageL2NormalizedWeight;
        this.accuracyContributionWeight = maxL2NormalizedWeight - minL2NormalizedWeight;
    }

    public void setAccuracyContributionRate(double accuracyContributionRate) {
        this.accuracyContributionRate = accuracyContributionRate;
    }

    public double getAccuracyContributionRate() {
        return accuracyContributionRate;
    }

    public double getAccuracyContributionWeight() {
        return accuracyContributionWeight;
    }

    public Module getDependentModule() {
        return dependentModule;
    }

    public Node getAccuracyContributionNode() {
        double maxDiff = maxL2NormalizedWeight - averageL2NormalizedWeight;
        double minDiff = averageL2NormalizedWeight - minL2NormalizedWeight;

        if (maxDiff >= minDiff) {
            return maxDependentNode;
        } else {
            return minDependentNode;
        }
    }

}

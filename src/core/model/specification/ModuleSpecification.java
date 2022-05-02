package core.model.specification;

import core.analyze.statics.NodeToModuleDependency;
import core.model.data.Module;
import core.model.data.Node;

import java.util.*;

public class ModuleSpecification {
    private static final double ACCURATE_BARRIER = 0.25;
    private static final double NORMAL_BARRIER = 0.5;

    private final Module module;
    private final Set<Node> specificationNodeSet;
    private final Set<Node> abnormalNodeSet;
    private final Map<Node, List<NodeToModuleDependency>> nodeDependencyVectorMap;
    private final Map<Module, Double> moduleAverageDependencyMap;
    private final Map<Module, Double> moduleMaxDependencyMap;
    private final Map<Module, Double> moduleMinDependencyMap;
    private double nonZeroDependencyAverageRange;
    private final double nonZeroRangeCount;
    private final double nonZeroRangeSum;
    private ModuleSpecificationAccuracy accuracy;
    private final Set<Module> allowedDependentModules;
    private final Set<ModuleSpecificationAccurayContribution> accurayContributions;

    @Override
    public String toString() {
        return "ModuleSpecification{" + "\n" +
                " module=" + module.getName() + ",\n" +
                " nodeDependencyVectorMap=" + NodeToModuleDependency.getNodeDependencyVectorInfo(1, nodeDependencyVectorMap) + ",\n" +
                " specificationNodeSet=" + specificationNodeSet + ",\n" +
                " abnormalNodeSet=" + abnormalNodeSet + ",\n" +
                " moduleAverageDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(moduleAverageDependencyMap) + ",\n" +
                " moduleMaxDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(moduleMaxDependencyMap) + ",\n" +
                " moduleMinDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(moduleMinDependencyMap) + ",\n" +
                " nonZeroDependencyAverageRange=" + nonZeroDependencyAverageRange + ",\n" +
                " nonZeroRangeSum=" + nonZeroRangeSum + ",\n" +
                " nonZeroRangeCount=" + nonZeroRangeCount + ",\n" +
                " accuracy=" + accuracy + "\n" +
                '}';
    }

    public ModuleSpecification(Module module, Set<Node> specificationNodeSet, Set<Node> abnormalNodeSet,
                               List<Module> otherModules, Map<Node, List<NodeToModuleDependency>> nodeDependencyVectorMap) {
        this.module = module;
        this.specificationNodeSet = specificationNodeSet;
        this.abnormalNodeSet = abnormalNodeSet;
        this.nodeDependencyVectorMap = nodeDependencyVectorMap;
        this.moduleAverageDependencyMap = new HashMap<>();
        this.moduleMaxDependencyMap = new HashMap<>();
        this.moduleMinDependencyMap = new HashMap<>();
        this.nonZeroDependencyAverageRange = 0;
        this.accuracy = null;
        this.allowedDependentModules = new HashSet<>();
        this.accurayContributions = new HashSet<>();

        int rangeCount = 0;
        double rangeSum = 0;

        /*
        模式生成：
        对于[规范集]中的所有向量，取每一个分量上的最大最小值，形成[外部依赖模式]在该分量上的区间
        */
        for (Module otherModule : otherModules) {
            double currentMax = 0;
            Node currentMaxNode = null;
            double currentMin = Double.MAX_VALUE;
            Node currentMinNode = null;
            double currentSum = 0;

            for (Node specifiationNode : specificationNodeSet) {
                double l2Dependency = NodeToModuleDependency.queryModuleDependencyByNodeAndEndModule(specifiationNode,
                        otherModule).getL2NormalizedWeight();
                currentSum += l2Dependency;
                if (l2Dependency > currentMax) {
                    currentMax = l2Dependency;
                    currentMaxNode = specifiationNode;
                }
                if (l2Dependency < currentMin) {
                    currentMin = l2Dependency;
                    currentMinNode = specifiationNode;
                }
            }

            double averageDependency = currentSum / specificationNodeSet.size();
            moduleAverageDependencyMap.put(otherModule, averageDependency);
            moduleMaxDependencyMap.put(otherModule, currentMax);
            moduleMinDependencyMap.put(otherModule, currentMin);

            if (currentMax > 0) {
                rangeCount++;
                rangeSum += currentMax - currentMin;
                allowedDependentModules.add(otherModule);
                accurayContributions.add(new ModuleSpecificationAccurayContribution(
                        otherModule, currentMaxNode, currentMax, currentMinNode, currentMin, averageDependency
                ));
            }
        }

        /*
        收敛水平：
        对于模式中在分量上区间非0值的所有分量，计算各个区间的跨度平均值——平均区间差异
                定义阈值α=0.25，β=0.5
                - 收敛：平均区间差异<=α
                - 分散：α<平均区间差异<=β
                - 稀疏：平均区间差异>β
        */
        nonZeroRangeCount = rangeCount;
        nonZeroRangeSum = rangeSum;
        if (rangeCount != 0) {
            nonZeroDependencyAverageRange = rangeSum / rangeCount;
            for (ModuleSpecificationAccurayContribution contribution : accurayContributions) {
                contribution.setAccuracyContributionRate(
                        (contribution.getAccuracyContributionWeight()) / rangeSum
                );
            }
        }

        if (nonZeroDependencyAverageRange <= ACCURATE_BARRIER) {
            accuracy = ModuleSpecificationAccuracy.ACCURATE;
        } else if (nonZeroDependencyAverageRange <= NORMAL_BARRIER) {
            accuracy = ModuleSpecificationAccuracy.NORMAL;
        } else {
            accuracy = ModuleSpecificationAccuracy.LOOSE;
        }
    }

    public ModuleSpecificationMatchResult matchSpecification(Node node) {
        boolean isMatch = true;
        double matchDistanceSquare = 0;

        for (Module module : moduleAverageDependencyMap.keySet()) {
            NodeToModuleDependency dependency = NodeToModuleDependency.queryModuleDependencyByNodeAndEndModule(node,
                    module);
            if (dependency.getL2NormalizedWeight() > moduleMaxDependencyMap.get(module) ||
                    dependency.getL2NormalizedWeight() < moduleMinDependencyMap.get(module)) {
                isMatch = false;
                matchDistanceSquare = 0;
                break;
            }
            matchDistanceSquare += Math.pow(dependency.getL2NormalizedWeight() - moduleAverageDependencyMap.get(module), 2);
        }

        return new ModuleSpecificationMatchResult(node, this, isMatch, Math.sqrt(matchDistanceSquare));
    }

    public Map<Node, List<NodeToModuleDependency>> getNodeDependencyVectorMap() {
        return nodeDependencyVectorMap;
    }

    public Module getModule() {
        return module;
    }

    public Set<Node> getSpecificationNodeSet() {
        return specificationNodeSet;
    }

    public Set<Node> getAbnormalNodeSet() {
        return abnormalNodeSet;
    }

    public Map<Module, Double> getModuleMaxDependencyMap() {
        return moduleMaxDependencyMap;
    }

    public Map<Module, Double> getModuleAverageDependencyMap() {
        return moduleAverageDependencyMap;
    }

    public Map<Module, Double> getModuleMinDependencyMap() {
        return moduleMinDependencyMap;
    }

    public double getNonZeroDependencyAverageRange() {
        return nonZeroDependencyAverageRange;
    }

    public ModuleSpecificationAccuracy getAccuracy() {
        return accuracy;
    }

    public double getNonZeroRangeCount() {
        return nonZeroRangeCount;
    }

    public double getNonZeroRangeSum() {
        return nonZeroRangeSum;
    }

    public Set<Module> getAllowedDependentModules() {
        return allowedDependentModules;
    }

    public Set<ModuleSpecificationAccurayContribution> getAccurayContributions() {
        return accurayContributions;
    }
}

package core.analyze.engine;

import core.analyze.statics.NodeToModuleDependency;
import core.model.data.Module;
import core.model.data.Node;
import core.model.specification.ModuleSpecification;

import java.util.*;

public class ModuleSpecificationAnalyzer {

    public ModuleSpecification matchModulePattern(Module module, List<Module> otherModules,
                                                  Set<Module> allowedDependentModules) {
        Map<Node, List<NodeToModuleDependency>> map = new HashMap<>();
        Set<Node> specificationNodeSet = new HashSet<>();
        Set<Node> abnormalNodeSet = new HashSet<>();

        for (Node node : module.getNodes()) {
            boolean divided = false;
            List<NodeToModuleDependency> nodeToModuleDependencyList = new ArrayList<>();
            for (Module otherModule : otherModules) {
                NodeToModuleDependency toModuleDependency =
                        NodeToModuleDependency.queryModuleDependencyByNodeAndEndModule(node, otherModule);
                if (!divided && toModuleDependency.getL2NormalizedWeight() != 0 && !allowedDependentModules.contains(otherModule)) {
                    abnormalNodeSet.add(node);
                    divided = true;
                }
                nodeToModuleDependencyList.add(toModuleDependency);
            }
            if (!divided) {
                specificationNodeSet.add(node);
            }
            map.put(node, nodeToModuleDependencyList);
        }

        ModuleSpecification moduleSpecification = new ModuleSpecification(module, specificationNodeSet,
                abnormalNodeSet, otherModules, map);
        module.setModuleSpecification(moduleSpecification);
        return moduleSpecification;
    }

    public ModuleSpecification analyzeModulePattern(Module module, List<Module> otherModules) {
        /*
        [A2]: 模块节点ModuleDependency向量模式发现算法
        算法输入：一组代表模块M内所有节点的外部依赖向量集合V
        例如 V = {<0.15, 0, 0.38, 0.2, 0>, <0.2, 0, 0.32, 0.27, 0>, <0, 0.35, 0, 0.05, 0>, <0.22, 0.05, 0.23, 0.4, 0>}
        该算法需要解决一个二分类问题，即将数据集V划分为[规范集]与[异常集]。其中规范集中的向量具有相似性，异常集中的向量与规范集中的向量
        相似性很低。([规范集]相当于符合ModuleSpecification的节点列表，[异常集]相当于不符合ModuleSpecification的节点列表)
        */
        Map<Node, List<NodeToModuleDependency>> map = new HashMap<>();
        for (Node node : module.getNodes()) {
            List<NodeToModuleDependency> nodeToModuleDependencyList = new ArrayList<>();
            for (Module otherModule : otherModules) {
                nodeToModuleDependencyList.add(NodeToModuleDependency.queryModuleDependencyByNodeAndEndModule(node, otherModule));
            }
            map.put(node, nodeToModuleDependencyList);
        }

        /*
        划分算法：
        定义规范集[Ce]与异常集[Ca]，其中异常集[Ca]可能为空集
                定义扩展系数β=0.25
        单次划分：
        STEP0: 定义集合Ce=∅, Ca=∅，所有向量的集合为V。
        STEP1: 向量p = 选取集合中的一个向量v。Ce = Ce + {v}, V = V - {v}。
        STEP2: 若V!=∅，计算V中所有与Ce中向量的距离，找出所有距离中最小的d。其中d=|v'-v| v'∈V, v∈Ce；否则进入STEP3。
        若d <= Ce中两点间距最大值*(1+β)，则Ce = Ce + {v'}, C = C - {v'}，重复STEP2;
        若不满足，则Ca = Ca + {v'}, C = C - {v'}，重复STEP2;
        STEP3: 得到集合Ce与Ca
        选取V中的每个v作为单次划分中STEP1的首个向量，重复STEP0-STEP4，每次得到一个Ce与Ca，返回出现次数最多的划分。
        */
        Map<Set<Node>, Integer> specificationNodeCountMap = new HashMap<>();
        int currentMaxCount = 0;
        Set<Node> currentMaxCountSpecificationNodeSet = new HashSet<>();
        Set<Node> currentMaxCountAbnormalNodeSet = new HashSet<>();

        final double distanceExpandParameter = 0.25;

        for (Node startNode : map.keySet()) {
            Set<Node> iterationNodeSet = new HashSet<>(map.keySet());   // V
            Set<Node> iterationSpecificationNodeSet = new HashSet<>();  // Ce
            double iterationSpecificationNodeSetMaxNodeDistance = 0;    // Ce中两点间距最大值
            Set<Node> iterationAbnormalNodeSet = new HashSet<>();       // Ca

            // STEP1: 向量p = 选取集合中的一个向量v。Ce = Ce + {v}, V = V - {v}。
            iterationNodeSet.remove(startNode);
            iterationSpecificationNodeSet.add(startNode);
            iterationSpecificationNodeSetMaxNodeDistance = Double.MAX_VALUE / 2;

            // STEP2 计算V中所有与Ce中向量的距离，找出所有距离中最小的d。其中d=|v'-v| v'∈V, v∈Ce
            while (iterationNodeSet.size() > 0) {
                Node currentNodeFromV = null;                           // v'
                double currentMinDistance = Double.MAX_VALUE;           // d
                List<Double> currentDistanceList = null;

                for (Node nodeFromV : iterationNodeSet) {
                    List<NodeToModuleDependency> vNodeList = map.get(nodeFromV);
                    List<Double> currentNodeFromVDistanceList = new ArrayList<>();
                    for (Node nodeFromCe : iterationSpecificationNodeSet) {
                        List<NodeToModuleDependency> ceNodeList = map.get(nodeFromCe);
                        double distance = NodeToModuleDependency.calculateDistance(vNodeList, ceNodeList);
                        currentNodeFromVDistanceList.add(distance);
                        if (distance < currentMinDistance) {
                            currentNodeFromV = nodeFromV;
                            currentMinDistance = distance;
                            currentDistanceList = currentNodeFromVDistanceList;
                        }
                    }
                }

                assert (currentNodeFromV != null);

                iterationNodeSet.remove(currentNodeFromV);
                if (currentMinDistance <= iterationSpecificationNodeSetMaxNodeDistance * (1 + distanceExpandParameter)) {
                    // 若d <= Ce中两点间距最大值*(1+β)，则Ce = Ce + {v'}, C = C - {v'}
                    iterationSpecificationNodeSet.add(currentNodeFromV);
                    // 更新 Ce中的两点间距离最大值
                    for (double d : currentDistanceList) {
                        if (d > iterationSpecificationNodeSetMaxNodeDistance) {
                            iterationSpecificationNodeSetMaxNodeDistance = d;
                        }
                    }
                } else {
                    // 若不满足，则Ca = Ca + {v'}, C = C - {v'}
                    iterationAbnormalNodeSet.addAll(iterationNodeSet);
                    iterationNodeSet.clear();
                }
            }

            int currentCeCount = 0;
            for (Set<Node> specificationNodeSet : specificationNodeCountMap.keySet()) {
                if (specificationNodeSet.equals(iterationSpecificationNodeSet)) {
                    currentCeCount = specificationNodeCountMap.get(specificationNodeSet) + 1;
                    specificationNodeCountMap.put(specificationNodeSet, currentCeCount);
                }
            }
            if (currentCeCount == 0) {
                currentCeCount = 1;
                specificationNodeCountMap.put(iterationSpecificationNodeSet, currentCeCount);
            }
            if (currentCeCount >= currentMaxCount) {
                currentMaxCount = currentCeCount;
                currentMaxCountSpecificationNodeSet = iterationSpecificationNodeSet;
                currentMaxCountAbnormalNodeSet = iterationAbnormalNodeSet;
            }
        }

        /*
        模式生成：
        对于[规范集]中的所有向量，取每一个分量上的最大最小值，形成[外部依赖模式]在该分量上的区间

        收敛水平：
        对于模式中在分量上区间非0值的所有分量，计算各个区间的跨度平均值——平均区间差异
                定义阈值α=0.2，β=0.4
                - 收敛：平均区间差异<=α
                - 分散：α<平均区间差异<=β
                - 稀疏：平均区间差异>β
        */
        ModuleSpecification moduleSpecification = new ModuleSpecification(module, currentMaxCountSpecificationNodeSet,
                currentMaxCountAbnormalNodeSet, otherModules, map);

        /*
        输出结果：
            [规范集]，[异常集]，[外部依赖模式], [收敛水平]
        例如 V = {<0.15, 0, 0.38, 0.2, 0>, <0.2, 0, 0.32, 0.27, 0>, <0, 0.35, 0, 0.05, 0>, <0.22, 0.05, 0.23, 0.4, 0>}
        那么各项输出为：
                [规范集] = {<0.15, 0, 0.38, 0.2, 0>, <0.2, 0, 0.32, 0.27, 0>, <0.22, 0.05, 0.23, 0.4, 0>}
                [异常集] = {<0, 0.35, 0, 0.05, 0>}
                [外部依赖模式] = <[0.15-0.22], [0-0.05], [0.23-0.38], [0.2-0.4], {0}>
                [平均区间差异] = (0.07 + 0.05 + 0.15 + 0.2) / 4 = 0.118
                [收敛水平] = 收敛
       */
        module.setModuleSpecification(moduleSpecification);
        return moduleSpecification;
    }
}

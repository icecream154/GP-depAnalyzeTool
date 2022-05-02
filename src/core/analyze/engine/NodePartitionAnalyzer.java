package core.analyze.engine;

import core.analyze.iteration.IterationAnalyzeResult;
import core.analyze.iteration.NodeMoveReason;
import core.analyze.iteration.NodeMoveResult;
import core.analyze.statics.NodeToModuleDependency;
import core.model.data.Module;
import core.model.data.Node;

import java.util.*;

public class NodePartitionAnalyzer {

    private static class TempModule {
        private final List<Node> nodes;
        private final List<List<NodeToModuleDependency>> nodeModuleDependencyVectorList;
        private final double acceptModuleDistance;

        public TempModule(Node node, List<NodeToModuleDependency> nodeModuleDependencyVector) {
            this.nodes = new ArrayList<>();
            this.nodes.add(node);
            this.nodeModuleDependencyVectorList = new ArrayList<>();
            this.nodeModuleDependencyVectorList.add(nodeModuleDependencyVector);
            this.acceptModuleDistance = calculateAcceptModuleDistance();
        }

        public TempModule(List<Node> nodes, List<List<NodeToModuleDependency>> nodeModuleDependencyVectorList) {
            this.nodes = nodes;
            this.nodeModuleDependencyVectorList = nodeModuleDependencyVectorList;
            this.acceptModuleDistance = calculateAcceptModuleDistance();
        }

        @Override
        public String toString() {
            Map<Node, List<NodeToModuleDependency>> tempMap = new HashMap<>();
            for (int i = 0; i < nodes.size(); i++) {
                tempMap.put(nodes.get(i), nodeModuleDependencyVectorList.get(i));
            }

            return "TempModule{" +
                    ", nodeModuleDependencyVectorList=" + NodeToModuleDependency.getNodeDependencyVectorInfo(0, tempMap) +
                    ", acceptModuleDistance=" + acceptModuleDistance +
                    '}';
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public List<List<NodeToModuleDependency>> getNodeModuleDependencyVectorList() {
            return nodeModuleDependencyVectorList;
        }

        public double getAcceptModuleDistance() {
            return acceptModuleDistance;
        }

        public double calculateAcceptModuleDistance() {
            if (nodeModuleDependencyVectorList.size() == 1) {
                return Double.MAX_VALUE;
            }

            double expandParameter = 0.25;
            if (nodes.size() <= 3) {
                expandParameter = 1.0 / nodes.size();
            }

            double currentMax = 0;
            for (int i = 0; i < nodeModuleDependencyVectorList.size() - 1; i++) {
                for (int j = i + 1; j < nodeModuleDependencyVectorList.size(); j++) {
                    double d = NodeToModuleDependency.calculateDistance(
                            nodeModuleDependencyVectorList.get(i), nodeModuleDependencyVectorList.get(j)
                    );
                    if (d > currentMax) {
                        currentMax = d;
                    }
                }
            }

            return currentMax * (1 + expandParameter);
        }

        // 合并模块
        public static TempModule combineTempModules(TempModule m1, TempModule m2) {
            List<Node> newNodes = new ArrayList<>(m1.getNodes());
            newNodes.addAll(m2.getNodes());
            List<List<NodeToModuleDependency>> newNodeModuleDependencyVectorList =
                    new ArrayList<>(m1.getNodeModuleDependencyVectorList());
            newNodeModuleDependencyVectorList.addAll(m2.getNodeModuleDependencyVectorList());
            return new TempModule(newNodes, newNodeModuleDependencyVectorList);
        }
    }

    private static class TempModuleDistance implements Comparable<TempModuleDistance> {
        private final TempModule m1;
        private final TempModule m2;
        private final double distance;

        public TempModuleDistance(TempModule m1, TempModule m2) {
            this.m1 = m1;
            this.m2 = m2;
            double min = Double.MAX_VALUE;
            double max = 0;

            for (int i = 0; i < m1.getNodeModuleDependencyVectorList().size(); i++) {
                for (int j = 0; j < m2.getNodeModuleDependencyVectorList().size(); j++) {
                    double tempDistance = NodeToModuleDependency.calculateDistance(
                            m1.getNodeModuleDependencyVectorList().get(i),
                            m2.getNodeModuleDependencyVectorList().get(j)
                    );
                    min = Math.min(min, tempDistance);
                    max = Math.max(max, tempDistance);
                }
            }
            this.distance = (min + max) / 2;
        }

        public TempModule getM1() {
            return m1;
        }

        public TempModule getM2() {
            return m2;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(TempModuleDistance t) {
            return Double.compare(this.distance, t.distance);
        }

    }

    public Set<Module> partitionSparseNodes(Set<Node> nodes, List<Module> referenceModules,
                                            IterationAnalyzeResult iterationAnalyzeResult) {
        /*
        *[A3]：稀疏节点模块划分算法
        算法输入：稀疏模块中节点构成的集合，对于集合中的每一个节点，取该节点对于收敛与分散模块的依赖组成节点的外部依赖向量。
        例如 V = {<0.44, 0.38, 0, 0>, <0.39, 0.46, 0, 0>, <0.39, 0.5, 0, 0>, <0, 0.1, 0.48, 0>, <0, 0.05, 0.54, 0>}

        基于模块合并的划分算法:
        初始情况下，每一个节点组成一个单独的模块M。定义两个模块P，Q间的距离为：(Min(d(p1, q1)) + Max(d(p2, q2))) / 2
        其中p1, p2∈P，q1, q2∈Q。

        定义扩展系数β=0.25
        迭代过程：
            STEP0: 计算所有模块间距离，其中模块P与Q之间的距离d(P, Q)是最小的。
            STEP1: 计算P或Q的接纳距离。对于当前仅包含单个向量的模块，接纳距离为+∞；对于当前包含多个节点的模块，
            接纳距离为模块内节点最大距离*(1+β)。
            STEP2：如果d <= min(P的接纳距离,Q的接纳距离)，合并P Q。否则取下一个模块间距离继续判断是否能够合并。
            如果没有可以合并的模块，迭代结束。

        输出结果:
        M1 = {<0.44, 0.38, 0, 0>, <0.39, 0.46, 0, 0>, <0.39, 0.5, 0, 0>}
        M2 = {<0, 0.1, 0.48, 0>, <0, 0.05, 0.54, 0>}
        */

        // 向量构造
        Map<Node, List<NodeToModuleDependency>> map = new HashMap<>();
        for (Node node : nodes) {
            List<NodeToModuleDependency> nodeToModuleDependencyList = new ArrayList<>();
            for (Module referenceModule : referenceModules) {
                nodeToModuleDependencyList.add(NodeToModuleDependency.queryModuleDependencyByNodeAndEndModule(node, referenceModule));
            }
            map.put(node, nodeToModuleDependencyList);
        }

        // 初始模块构造
        Set<TempModule> tempModules = new HashSet<>();
        for (Node node : nodes) {
            tempModules.add(new TempModule(node, map.get(node)));
        }

        boolean continueCombine = true;
        while (continueCombine) {
            List<TempModule> currentModuleList = new ArrayList<>(tempModules);

            PriorityQueue<TempModuleDistance> distancePriorityQueue = new PriorityQueue<>();
            for (int i = 0; i < currentModuleList.size() - 1; i++) {
                for (int j = i + 1; j < currentModuleList.size(); j++) {
                    distancePriorityQueue.add(new TempModuleDistance(currentModuleList.get(i), currentModuleList.get(j)));
                }
            }

            while (!distancePriorityQueue.isEmpty()) {
                TempModuleDistance tempModuleDistance = distancePriorityQueue.poll();
                if (tempModuleDistance.getDistance() <= tempModuleDistance.getM1().getAcceptModuleDistance()
                        && tempModuleDistance.getDistance() <= tempModuleDistance.getM2().getAcceptModuleDistance()) {
                    tempModules.remove(tempModuleDistance.getM1());
                    tempModules.remove(tempModuleDistance.getM2());
                    tempModules.add(TempModule.combineTempModules(tempModuleDistance.getM1(), tempModuleDistance.getM2()));
                    break;
                }
            }

            // 若没有可以合并的模块，迭代结束
            continueCombine = !distancePriorityQueue.isEmpty();
        }

        // 结果集返回
        Set<Module> newModules = new HashSet<>();
        int index = 0;
        for (TempModule tempModule : tempModules) {
            Module newModule = new Module("NewModule(" + index + ")", "", new HashSet<>(), false);
            for (Node node : tempModule.getNodes()) {
                iterationAnalyzeResult.getNodeMoveResults().add(
                        new NodeMoveResult(node, node.getModule(), newModule, NodeMoveReason.RECOMBINE)
                );
                node.setModule(newModule);
            }

            newModules.add(newModule);
            index++;
        }
        return newModules;
    }
}

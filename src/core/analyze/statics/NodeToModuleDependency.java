package core.analyze.statics;

import core.model.data.Edge;
import core.model.data.Graph;
import core.model.data.Node;
import core.model.data.Module;

import java.text.DecimalFormat;
import java.util.*;

public class NodeToModuleDependency {
    private final Node node;
    private final Module endModule;
    private final Set<Edge> relatedEdges;
    private double allWeightOfRelatedEdges;
    private double l2NormalizedWeight;

    private static Map<Node, Set<NodeToModuleDependency>> nodeQueryMap;
    private static Map<Module, Set<NodeToModuleDependency>> moduleQueryMap;
    private static Map<Module, Double> moduleSumSqrtMap;

    @Override
    public String toString() {
        return "NodeToModuleDependency{" +
                "node=" + node +
                ", endModule=" + endModule +
                ", relatedEdges=" + relatedEdges +
                ", allWeightOfRelatedEdges=" + allWeightOfRelatedEdges +
                ", l2NormalizedWeight=" + l2NormalizedWeight +
                '}';
    }

    public static double calculateDistance(List<NodeToModuleDependency> n1List, List<NodeToModuleDependency> n2List) {
        assert (n1List.size() == n2List.size());
        double squareSum = 0;
        for (int i = 0; i < n1List.size(); i++) {
            squareSum += Math.pow(n1List.get(i).getL2NormalizedWeight() - n2List.get(i).getL2NormalizedWeight(), 2);
        }
        return Math.sqrt(squareSum);
    }

    public static String getL2NormalizedWeightList(List<NodeToModuleDependency> nodeToModuleDependencyList) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String res = "<";
        for (NodeToModuleDependency nodeToModuleDependency : nodeToModuleDependencyList) {
            res += decimalFormat.format(nodeToModuleDependency.getL2NormalizedWeight()) + ",";
        }
        res = res.substring(0, res.length() - 1);
        res += ">";
        return res;
    }

    public static String getNodeDependencyVectorInfo(int indentNumber, Map<Node, List<NodeToModuleDependency>> nodeDependencyVectorMap) {
        String indent = "";
        for (int i = 0; i < indentNumber; i++) {
            indent += "\t";
        }
        String res = "{\n";
        for (Node node : nodeDependencyVectorMap.keySet()) {
            List<NodeToModuleDependency> dependencyList = nodeDependencyVectorMap.get(node);
            res += indent + "(" + node.getName() + ":" + NodeToModuleDependency.getL2NormalizedWeightList(dependencyList) + "),\n";
        }
        res = res.substring(0, res.length() - 1);
        res += "\n" + indent;
        return res.substring(0, res.length() - 1) + "}";
    }

    public static String getDependencyMapInfo(Map<Module, Double> dependencyMap) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String res = "{";
        for (Module module : dependencyMap.keySet()) {
            res += "(" + module.getName() + ":" + decimalFormat.format(dependencyMap.get(module)) + "),";
        }
        res = res.substring(0, res.length() - 1);
        res += "}";
        return res;
    }

    public static String getDependencyMapVectorInfo(Map<Module, Double> dependencyMap) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String res = "<";
        for (Module module : dependencyMap.keySet()) {
            res += decimalFormat.format(dependencyMap.get(module)) + ",";
        }
        res = res.substring(0, res.length() - 1);
        res += ">";
        return res;
    }

    public static void register(Graph graph) {
        // 初始化查询 map
        nodeQueryMap = new HashMap<>();
        moduleQueryMap = new HashMap<>();
        moduleSumSqrtMap = new HashMap<>();
        for (Node n : graph.getNodes()) {
            nodeQueryMap.put(n, new HashSet<>());
        }
        for (Module module : graph.getModules()) {
            moduleQueryMap.put(module, new HashSet<>());
        }

        // 构建ModuleDependency对象
        for (Node n : graph.getNodes()) {
            for (Module module : graph.getModules()) {
                if (n.getModule() != module) {
                    NodeToModuleDependency nodeToModuleDependency = new NodeToModuleDependency(n, module);
                    // System.out.println("Node to module dependency built for: Node=" + n.getName() + " module=" + module.getName());
                    nodeQueryMap.get(n).add(nodeToModuleDependency);
                    moduleQueryMap.get(module).add(nodeToModuleDependency);
                }
            }
        }

        //遍历每一个Module，对依赖程度进行L2归一化
        for (Module module : graph.getModules()) {
            List<NodeToModuleDependency> nonZeroModuleDependencies = new ArrayList<>();
            double squareSum = 0;

            for (NodeToModuleDependency nd : queryModuleDependencyByEndModule(module)) {
                if (nd.getRelatedEdges().size() == 0) {
                    nd.setL2NormalizedWeight(0);
                } else {
                    nonZeroModuleDependencies.add(nd);
                    squareSum += Math.pow(nd.getAllWeightOfRelatedEdges(), 2);
                }
            }

            //L2归一化
            double sqrtSum = Math.sqrt(squareSum);
            moduleSumSqrtMap.put(module, sqrtSum);
            for (NodeToModuleDependency nd : nonZeroModuleDependencies) {
                nd.setL2NormalizedWeight(nd.getAllWeightOfRelatedEdges() / sqrtSum);
            }
        }
    }

    public static Set<NodeToModuleDependency> queryModuleDependencyByNode(Node node) {
        return nodeQueryMap.get(node);
    }

    public static Set<NodeToModuleDependency> queryModuleDependencyByEndModule(Module module) {
        return moduleQueryMap.get(module);
    }

    public static NodeToModuleDependency queryModuleDependencyByNodeAndEndModule(Node node, Module module) {
        if (node.getModule() == module) {
            NodeToModuleDependency nd = new NodeToModuleDependency(node, module);
            nd.setL2NormalizedWeight(nd.getAllWeightOfRelatedEdges() / moduleSumSqrtMap.get(module));
            return nd;
        }
        Set<NodeToModuleDependency> s = nodeQueryMap.get(node);
        for (NodeToModuleDependency nodeToModuleDependency : s) {
            if (nodeToModuleDependency.getEndNodeModule() == module) {
                return nodeToModuleDependency;
            }
        }
        System.out.println("Null returned for Node: " + node.getName() + " Node's Module: " + node.getModule().getName() + " Module: " + module.getName());
        return null;
    }

    private NodeToModuleDependency(Node node, Module endModule) {
        this.node = node;
        this.endModule = endModule;
        this.relatedEdges = new HashSet<>();
        this.allWeightOfRelatedEdges = 0;
        this.l2NormalizedWeight = 0;
        for (Edge e : node.getOutcomingEdges()) {
            if (endModule.getNodes().contains(e.getEndNode())) {
                relatedEdges.add(e);
                allWeightOfRelatedEdges += e.getWeight();
            }
        }
    }

    private double getAllWeightOfRelatedEdges() {
        return allWeightOfRelatedEdges;
    }

    public Node getNode() {
        return node;
    }

    public Module getEndNodeModule() {
        return endModule;
    }

    public Set<Edge> getRelatedEdges() {
        return relatedEdges;
    }

    public double getL2NormalizedWeight() {
        return l2NormalizedWeight;
    }

    private void setL2NormalizedWeight(double l2NormalizedWeight) {
        this.l2NormalizedWeight = l2NormalizedWeight;
    }
}

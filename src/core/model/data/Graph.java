package core.model.data;

import java.util.*;

public class Graph {
    private Set<Node> nodes;
    private Set<Edge> edges;
    private Set<Module> modules;
    private Map<String, Module> nameModuleMap;

    public Graph(Set<Node> nodes, Set<Edge> edges, Set<Module> initModules) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
        this.modules = new HashSet<>(initModules);
        nameModuleMap = new HashMap<>();
        for (Module module : modules) {
            nameModuleMap.put(module.getName(), module);
        }
    }

    public Module getModuleByName(String name) {
        return nameModuleMap.get(name);
    }

    public Graph clone() {
        Map<String, Node> newNodeMap = new HashMap<>();
        for (Node node : nodes) {
            newNodeMap.put(node.getName(), new Node(node.getIndex(), node.getName(), node.getReferenceObject()));
        }

        Set<Edge> newEdgeSet = new HashSet<>();
        for (Edge edge : edges) {
            newEdgeSet.add(new Edge(newNodeMap.get(edge.getStartNode().getName()),
                    newNodeMap.get(edge.getEndNode().getName()), edge.getWeight()));
        }

        Set<Module> modules = new HashSet<>();
        for (Module module : this.modules) {
            Set<Node> moduleNodes = new HashSet<>();
            for (Node node : module.getNodes()) {
                moduleNodes.add(newNodeMap.get(node.getName()));
            }
            modules.add(new Module(module.getName(), moduleNodes, module.isFromInitGraph()));
        }
        return new Graph(new HashSet(newNodeMap.values()), newEdgeSet, modules);
    }

    public Set<Node> getNodes() {
        return nodes;
    }
    public Set<Edge> getEdges() {
        return edges;
    }
    public Set<Module> getModules() {
        return modules;
    }

    public Set<Module> clearEmptyModules() {
        Set<Module> emptyModules = new HashSet<>();
        for (Module module : modules) {
            if (module.getNodes().isEmpty()) {
                emptyModules.add(module);
            }
        }
        modules.removeAll(emptyModules);

        nameModuleMap.clear();
        for (Module module : modules) {
            nameModuleMap.put(module.getName(), module);
        }
        return emptyModules;
    }
}
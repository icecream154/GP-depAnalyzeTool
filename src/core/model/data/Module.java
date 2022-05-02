package core.model.data;

import core.model.specification.ModuleSpecification;

import java.util.HashSet;
import java.util.Set;

public class Module {
    private final String name;
    private final String path;
    private final Set<Node> nodes;
    private final boolean fromInitGraph;
    private ModuleSpecification moduleSpecification;

    public Module(String name, String path, Set<Node> nodes, boolean fromInitGraph) {
        this.name = name;
        this.path = path;
        this.nodes = new HashSet<>();
        this.fromInitGraph = fromInitGraph;
        for (Node node : nodes) {
            node.setModule(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public String[] getNodeNames() {
        String[] res = new String[nodes.size()];
        int i = 0;
        for (Node node : nodes) {
            res[i] = node.getName();
            i++;
        }
        return res;
    }

    public boolean isFromInitGraph() {
        return fromInitGraph;
    }

    public ModuleSpecification getModuleSpecification() {
        return moduleSpecification;
    }

    public void setModuleSpecification(ModuleSpecification moduleSpecification) {
        this.moduleSpecification = moduleSpecification;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes.size() +
                '}';
    }
}

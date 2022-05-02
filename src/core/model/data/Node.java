package core.model.data;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private int index;
    private String name;
    private Set<Edge> incomingEdges;
    private Set<Edge> outcomingEdges;
    private Module module;
    private NodeReferenceObject referenceObject;

    public Node(int index, String name, NodeReferenceObject referenceObject) {
        this.index = index;
        this.name = name;
        this.incomingEdges = new HashSet<>();
        this.outcomingEdges = new HashSet<>();
        this.referenceObject = referenceObject;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", module='" + module.getName() + '\'' +
                '}';
    }

    public void setModule(Module newModule) {
        if (this.module != null) {
            this.module.getNodes().remove(this);
        }
        newModule.getNodes().add(this);
        this.module = newModule;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    public Set<Edge> getOutcomingEdges() {
        return outcomingEdges;
    }

    public Module getModule() {
        return module;
    }

    public NodeReferenceObject getReferenceObject() {
        return referenceObject;
    }
}
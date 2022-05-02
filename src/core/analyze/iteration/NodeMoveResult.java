package core.analyze.iteration;

import core.model.data.Module;
import core.model.data.Node;

public class NodeMoveResult {
    private Node node;
    private Module sourceModule;
    private Module targetModule;
    private NodeMoveReason nodeMoveReason;

    public NodeMoveResult(Node node, Module sourceModule, Module targetModule, NodeMoveReason nodeMoveReason) {
        this.node = node;
        this.sourceModule = sourceModule;
        this.targetModule = targetModule;
        this.nodeMoveReason = nodeMoveReason;
    }

    public Node getNode() {
        return node;
    }

    public Module getSourceModule() {
        return sourceModule;
    }

    public Module getTargetModule() {
        return targetModule;
    }

    public NodeMoveReason getNodeMoveReason() {
        return nodeMoveReason;
    }
}

package core.model.specification;

import core.model.data.Node;

public record ModuleSpecificationMatchResult(Node node,
                                             ModuleSpecification moduleSpecification,
                                             boolean isMatch, double matchDistance) {

    public Node getNode() {
        return node;
    }

    public ModuleSpecification getModuleSpecification() {
        return moduleSpecification;
    }

    public double getMatchDistance() {
        return matchDistance;
    }
}

package core.model.data;

public class Edge {
    private Node startNode;
    private Node endNode;
    private double weight;

    public Edge(Node startNode, Node endNode, double weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
        startNode.getOutcomingEdges().add(this);
        endNode.getIncomingEdges().add(this);
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "startNode={" + startNode.getName() + "}" +
                ", endNode={" + endNode.getName() + "}" +
                ", weight=" + weight +
                '}';
    }
}

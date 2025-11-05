package app.model;

public class RouteEdge {
    private final String originId;
    private final String destinationId;
    private final double weight;

    public RouteEdge(String originId, String destinationId, double weight) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.weight = weight;
    }

    public String getOriginId() {
        return originId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public double getWeight() {
        return weight;
    }
}

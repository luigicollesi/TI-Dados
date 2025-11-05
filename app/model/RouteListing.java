package app.model;

public class RouteListing {
    private final String id;
    private final String destinationName;
    private final double distanceKm;

    public RouteListing(String id, String destinationName, double distanceKm) {
        this.id = id;
        this.destinationName = destinationName;
        this.distanceKm = distanceKm;
    }

    public String getId() {
        return id;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    @Override
    public String toString() {
        return destinationName + " (" + String.format("%.0f km", distanceKm) + ")";
    }
}

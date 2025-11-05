package app.model;

import java.util.Collections;
import java.util.List;

public class RouteResult {
    private final List<Destination> path;
    private final double totalDistance;

    public RouteResult(List<Destination> path, double totalDistance) {
        this.path = path;
        this.totalDistance = totalDistance;
    }

    public static RouteResult empty() {
        return new RouteResult(Collections.emptyList(), Double.POSITIVE_INFINITY);
    }

    public List<Destination> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public boolean hasPath() {
        return !path.isEmpty();
    }
}

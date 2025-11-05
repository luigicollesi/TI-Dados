package app.graph;

import app.model.Destination;
import app.model.RouteEdge;
import app.model.RouteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class RouteGraph {
    private final List<Destination> destinations;
    private final Map<String, Integer> indexById;
    private final List<List<Edge>> adjacency;

    public RouteGraph(List<Destination> destinations, List<RouteEdge> edges) {
        this.destinations = new ArrayList<>(destinations);
        this.indexById = new HashMap<>();
        this.adjacency = new ArrayList<>(destinations.size());

        for (int i = 0; i < destinations.size(); i++) {
            indexById.put(destinations.get(i).getId(), i);
            adjacency.add(new ArrayList<>());
        }

        for (RouteEdge edge : edges) {
            Integer from = indexById.get(edge.getOriginId());
            Integer to = indexById.get(edge.getDestinationId());
            if (from == null || to == null) {
                continue;
            }
            addUndirectedEdge(from, to, edge.getWeight());
        }
    }

    public RouteResult findShortestPath(String originId, String destinationId) {
        Integer source = indexById.get(originId);
        Integer target = indexById.get(destinationId);

        if (source == null || target == null) {
            return RouteResult.empty();
        }

        if (source.equals(target)) {
            Destination destination = destinations.get(source);
            return new RouteResult(Collections.singletonList(destination), 0.0);
        }

        int n = destinations.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        dist[source] = 0.0;

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.distance));
        pq.offer(new Node(source, 0.0));

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (visited[node.index]) {
                continue;
            }
            visited[node.index] = true;

            if (node.index == target) {
                break;
            }

            for (Edge edge : adjacency.get(node.index)) {
                double candidate = dist[node.index] + edge.weight;
                if (candidate < dist[edge.to]) {
                    dist[edge.to] = candidate;
                    prev[edge.to] = node.index;
                    pq.offer(new Node(edge.to, candidate));
                }
            }
        }

        if (Double.isInfinite(dist[target])) {
            return RouteResult.empty();
        }

        List<Destination> path = new ArrayList<>();
        for (int at = target; at != -1; at = prev[at]) {
            path.add(destinations.get(at));
        }
        Collections.reverse(path);

        return new RouteResult(path, dist[target]);
    }

    private void addUndirectedEdge(int from, int to, double weight) {
        adjacency.get(from).add(new Edge(to, weight));
        adjacency.get(to).add(new Edge(from, weight));
    }

    private static class Edge {
        final int to;
        final double weight;

        Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private static class Node {
        final int index;
        final double distance;

        Node(int index, double distance) {
            this.index = index;
            this.distance = distance;
        }
    }
}

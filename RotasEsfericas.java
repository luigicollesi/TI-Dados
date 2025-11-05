import java.util.*;
import static java.lang.Math.*;

class Geo {
    static final double R = 6371.0;

    static double deg2rad(double d) { return d * Math.PI / 180.0; }

    static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double φ1 = deg2rad(lat1), λ1 = deg2rad(lon1);
        double φ2 = deg2rad(lat2), λ2 = deg2rad(lon2);
        double dφ = φ2 - φ1, dλ = ((λ2 - λ1 + Math.PI) % (2*Math.PI)) - Math.PI;
        double a = sin(dφ/2)*sin(dφ/2) + cos(φ1)*cos(φ2)*sin(dλ/2)*sin(dλ/2);
        double c = 2*asin(min(1.0, sqrt(a)));
        return R * c;
    }
}

class Edge {
    final int to;
    final double w;
    Edge(int to, double w) { this.to = to; this.w = w; }
}

class Graph {
    final List<List<Edge>> adj;
    Graph(int n) { adj = new ArrayList<>(Collections.nCopies(n, null)); 
                   for (int i=0;i<n;i++) adj.set(i, new ArrayList<>()); }
    void addUndir(int u, int v, double w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }
    List<Integer> dijkstra(int s, int t) {
        int n = adj.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[s] = 0.0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> dist[a[0]]));
        pq.add(new int[]{s});
        boolean[] vis = new boolean[n];
        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (vis[u]) continue;
            vis[u] = true;
            if (u == t) break;
            for (Edge e : adj.get(u)) {
                if (dist[u] + e.w < dist[e.to]) {
                    dist[e.to] = dist[u] + e.w;
                    prev[e.to] = u;
                    pq.add(new int[]{e.to});
                }
            }
        }
        List<Integer> path = new ArrayList<>();
        if (prev[t] == -1 && s != t) return path; // vazio = sem caminho
        for (int v = t; v != -1; v = prev[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }
}

public class RotasEsfericas {
    static class City { String name; double lat, lon; City(String n,double la,double lo){name=n;lat=la;lon=lo;} }

    static void add(City[] C, Graph g, int a, int b) {
    double w = Geo.haversineKm(C[a].lat, C[a].lon, C[b].lat, C[b].lon);
    g.addUndir(a, b, w);
}

    public static void main(String[] args) {
        City[] C = new City[]{
            new City("São Paulo", -23.5505, -46.6333),
            new City("New York",   40.7128, -74.0060),
            new City("Tokyo",      35.6764, 139.6500),
            new City("Berlin",     52.5200,  13.4050),
            new City("Moscow",     55.7558,  37.6173),
            new City("Sydney",      -33.8688,  151.2093),
            new City("Cidade do Cabo", -33.9249, 18.4241),
            new City("Buenos Aires", -34.6037, -58.3816),
            new City("Toronto",      43.6532,  -79.3832),
            new City("Nova Délhi",   28.6139,   77.2090)
        };
        int n = C.length;
        Graph g = new Graph(n);

        // Ex.: grafo completo (ou restrinja por “voos diretos” que você definir)
// Rotas Reais
add(C, g, 0,7); // São Paulo ↔ Buenos Aires
add(C, g, 0,1); // São Paulo ↔ New York
add(C, g, 0,6); // São Paulo ↔ Cidade do Cabo
add(C, g, 1,8); // New York ↔ Toronto
add(C, g, 1,3); // New York ↔ Berlin
add(C, g, 3,4); // Berlin ↔ Moscow
add(C, g, 2,5); // Tokyo ↔ Sydney
add(C, g, 2,9); // Tokyo ↔ Nova Délhi
add(C, g, 5,6); // Sydney ↔ Cidade do Cabo

// Rotas Didáticas (destacam geodésicas)
add(C, g, 7,2); // Buenos Aires ↔ Tokyo (didático)
add(C, g, 4,2); // Moscow ↔ Tokyo (didático)
add(C, g, 3,9); // Berlin ↔ Nova Délhi (didático)


        // Exemplo de rota mínima: São Paulo -> Tokyo
        int s = 0, t = 4;
        List<Integer> path = g.dijkstra(s, t);
        System.out.println("Melhor rota (grande-círculo) de " + C[s].name + " a " + C[t].name + ":");
        double total = 0;
        for (int i=0;i<path.size();i++) {
            System.out.print(C[path.get(i)].name);
            if (i+1 < path.size()) {
                double seg = Geo.haversineKm(
                    C[path.get(i)].lat, C[path.get(i)].lon,
                    C[path.get(i+1)].lat, C[path.get(i+1)].lon
                );
                total += seg;
                System.out.print(" -> ");
            }
        }
        System.out.println("\nDistância total (km): " + Math.round(total));
    }
}

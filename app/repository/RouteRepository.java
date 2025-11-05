package app.repository;

import app.db.DatabaseConnection;
import app.db.DatabaseOperations;
import app.model.Destination;
import app.model.RouteEdge;
import app.model.RouteListing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {

    public List<Destination> loadDestinations() {
        List<Destination> destinations = new ArrayList<>();
        ResultSet rs = DatabaseOperations.executeQuery(
            "SELECT id, nome, latitude, longitude FROM destinos ORDER BY nome",
            null
        );

        if (rs == null) {
            return destinations;
        }

        Statement statement = null;
        Connection connection = null;
        try {
            statement = rs.getStatement();
            connection = statement != null ? statement.getConnection() : null;
            while (rs.next()) {
                destinations.add(
                    new Destination(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, statement, connection);
        }

        return destinations;
    }

    public List<RouteEdge> loadConnections() {
        List<RouteEdge> edges = new ArrayList<>();
        ResultSet rs = DatabaseOperations.executeQuery(
            "SELECT origem_id, destino_id, peso_km FROM conexoes",
            null
        );

        if (rs == null) {
            return edges;
        }

        Statement statement = null;
        Connection connection = null;
        try {
            statement = rs.getStatement();
            connection = statement != null ? statement.getConnection() : null;
            while (rs.next()) {
                edges.add(
                    new RouteEdge(
                        rs.getString("origem_id"),
                        rs.getString("destino_id"),
                        rs.getDouble("peso_km")
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, statement, connection);
        }

        return edges;
    }

    public boolean addDestination(String name, double latitude, double longitude) {
        String sql = "INSERT INTO destinos (nome, latitude, longitude) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, latitude);
            ps.setDouble(3, longitude);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean destinationExistsByName(String name) {
        String sql = "SELECT 1 FROM destinos WHERE LOWER(nome) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDestination(String destinationId) {
        String sql = "DELETE FROM destinos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, destinationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double existingRouteDistance(String originId, String destinationId) {
        String sql = """
            SELECT peso_km FROM conexoes
             WHERE (origem_id = ? AND destino_id = ?)
                OR (origem_id = ? AND destino_id = ?)
             LIMIT 1
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, originId);
            ps.setString(2, destinationId);
            ps.setString(3, destinationId);
            ps.setString(4, originId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("peso_km");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRoute(String originId, String destinationId, double distanceKm) {
        String sql = "INSERT INTO conexoes (origem_id, destino_id, peso_km) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, originId);
            ps.setString(2, destinationId);
            ps.setDouble(3, distanceKm);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RouteListing> loadRoutesFrom(String originId) {
        List<RouteListing> routes = new ArrayList<>();
        String sql = """
            SELECT c.id, d.nome AS destino_nome, c.peso_km
              FROM conexoes c
              JOIN destinos d ON d.id = c.destino_id
             WHERE c.origem_id = ?
             ORDER BY d.nome
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, originId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    routes.add(new RouteListing(
                        rs.getString("id"),
                        rs.getString("destino_nome"),
                        rs.getDouble("peso_km")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public boolean deleteRoute(String routeId) {
        String sql = "DELETE FROM conexoes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void closeResources(ResultSet rs, Statement statement, Connection connection) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException ignored) {}

        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException ignored) {}

        DatabaseConnection.close(connection);
    }
}

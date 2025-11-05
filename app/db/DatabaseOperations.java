package app.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseOperations {

    public static void executeUpdate(String sql, String[] params) {
        Connection conn  = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setString(i + 1, params[i]);
                }
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps  != null) ps.close();  } catch (SQLException ignored) {}
            DatabaseConnection.close(conn);
        }
    }

    public static ResultSet executeQuery(String sql, String[] params) {
        Connection conn  = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps   = conn.prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setString(i + 1, params[i]);
                }
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            DatabaseConnection.close(conn);
            return null;
        }
    }
}

package app;

import app.db.DatabaseConnection;
import app.ui.routes.RoutePlannerFrame;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;

public class AppMain {

    public static void main(String[] args) {
        configureLookAndFeel();
        setupOptionPaneFonts();

        try (Connection ignored = DatabaseConnection.getConnection()) {
            EventQueue.invokeLater(() -> new RoutePlannerFrame().setVisible(true));
        } catch (SQLException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Não foi possível conectar ao banco de dados:\n" + ex.getMessage(),
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE
            ));
            System.exit(1);
        }
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                 InstantiationException | IllegalAccessException ignored) {}
    }

    private static void setupOptionPaneFonts() {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 25));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 25));
    }
}

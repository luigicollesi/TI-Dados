package app.ui.routes;

import app.ui.theme.AviationTheme;
import app.ui.utility.OutlineLabel;
import app.ui.utility.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RouteResultDialog extends JDialog {

    public RouteResultDialog(Window owner, String routePath, double totalDistanceKm) {
        super(owner, "Rota otimizada", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setSize(620, 360);
        setLocationRelativeTo(owner);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(routePath, totalDistanceKm), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 24, 0, 24));

        OutlineLabel title = new OutlineLabel(
            "Rota mais rápida encontrada",
            new Font("Arial", Font.BOLD, 30),
            Color.WHITE
        );
        header.add(title, BorderLayout.WEST);

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        header.add(closeButton, BorderLayout.EAST);

        return header;
    }

    private JPanel buildContent(String routePath, double totalDistanceKm) {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel planeIcon = new JLabel("✈", SwingConstants.CENTER);
        planeIcon.setFont(new Font("Arial", Font.BOLD, 60));
        planeIcon.setForeground(AviationTheme.PRIMARY_LIGHT);
        center.add(planeIcon, BorderLayout.NORTH);

        String formattedRoute = "<html><center><span style='font-size:24px;font-weight:bold;color:#f0f8ff;'>"
            + routePath
            + "</span></center></html>";

        JLabel routeLabel = new JLabel(formattedRoute, SwingConstants.CENTER);
        routeLabel.setBorder(new EmptyBorder(20, 0, 15, 0));
        center.add(routeLabel, BorderLayout.CENTER);

        JLabel distanceLabel = new JLabel(
            String.format("Distância total: %.2f km", totalDistanceKm),
            SwingConstants.CENTER
        );
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        distanceLabel.setForeground(new Color(235, 245, 255));
        center.add(distanceLabel, BorderLayout.SOUTH);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 22, 0));

        RoundButton close = new RoundButton("Fechar", AviationTheme.PRIMARY_DARK);
        close.setFont(new Font("Arial", Font.BOLD, 24));
        close.setPreferredSize(new Dimension(180, 60));
        close.addActionListener(e -> dispose());
        footer.add(close);

        return footer;
    }

    private static class FrameDragListener extends MouseAdapter {
        private Point origin;

        @Override
        public void mousePressed(MouseEvent e) {
            origin = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (origin != null) {
                Window window = SwingUtilities.getWindowAncestor(e.getComponent());
                if (window != null) {
                    Point current = e.getLocationOnScreen();
                    window.setLocation(current.x - origin.x, current.y - origin.y);
                }
            }
        }
    }
}

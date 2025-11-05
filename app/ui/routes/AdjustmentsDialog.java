package app.ui.routes;

import app.repository.RouteRepository;
import app.ui.theme.AviationTheme;
import app.ui.utility.OutlineLabel;
import app.ui.utility.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdjustmentsDialog extends JDialog {

    private final RouteRepository repository;
    private final Runnable onDataChanged;

    public AdjustmentsDialog(Frame owner, RouteRepository repository, Runnable onDataChanged) {
        super(owner, "Ajustes de Rotas", true);
        this.repository = repository;
        this.onDataChanged = onDataChanged;

        setUndecorated(true);
        setSize(520, 540);
        setLocationRelativeTo(owner);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        OutlineLabel title = new OutlineLabel(
            "Ajustes de rotas",
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
        closeButton.setOpaque(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        header.add(closeButton, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout());
        center.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new GridLayout(4, 1, 0, 18));

        RoundButton addDestination = new RoundButton("Adicionar destino", AviationTheme.PRIMARY_DARK);
        addDestination.setFont(new Font("Arial", Font.BOLD, 24));
        addDestination.setPreferredSize(new Dimension(320, 60));
        addDestination.addActionListener(e -> openAddDestination());
        listPanel.add(addDestination);

        RoundButton addRoute = new RoundButton("Adicionar rota", AviationTheme.PRIMARY_DARK);
        addRoute.setFont(new Font("Arial", Font.BOLD, 24));
        addRoute.setPreferredSize(new Dimension(320, 60));
        addRoute.addActionListener(e -> openAddRoute());
        listPanel.add(addRoute);

        RoundButton deleteDestination = new RoundButton("Deletar destino", AviationTheme.PRIMARY_DARK);
        deleteDestination.setFont(new Font("Arial", Font.BOLD, 24));
        deleteDestination.setPreferredSize(new Dimension(320, 60));
        deleteDestination.addActionListener(e -> openDeleteDestination());
        listPanel.add(deleteDestination);

        RoundButton deleteRoute = new RoundButton("Deletar rota", AviationTheme.PRIMARY_DARK);
        deleteRoute.setFont(new Font("Arial", Font.BOLD, 24));
        deleteRoute.setPreferredSize(new Dimension(320, 60));
        deleteRoute.addActionListener(e -> openDeleteRoute());
        listPanel.add(deleteRoute);

        center.add(listPanel, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 25, 18, 25));

        JLabel note = new JLabel("Gerencie os destinos e conexões da malha aérea.");
        note.setFont(new Font("Arial", Font.PLAIN, 15));
        note.setForeground(new Color(230, 240, 252));
        footer.add(note, BorderLayout.WEST);

        RoundButton backButton = new RoundButton("Voltar", AviationTheme.PRIMARY_DARK);
        backButton.setPreferredSize(new Dimension(130, 50));
        backButton.addActionListener(e -> dispose());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(backButton);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private void openAddDestination() {
        AddDestinationDialog dialog = new AddDestinationDialog(this, repository, notifyChanges());
        dialog.setVisible(true);
    }

    private void openAddRoute() {
        AddRouteDialog dialog = new AddRouteDialog(this, repository, notifyChanges());
        dialog.setVisible(true);
    }

    private void openDeleteDestination() {
        DeleteDestinationDialog dialog = new DeleteDestinationDialog(this, repository, notifyChanges());
        dialog.setVisible(true);
    }

    private void openDeleteRoute() {
        DeleteRouteDialog dialog = new DeleteRouteDialog(this, repository, notifyChanges());
        dialog.setVisible(true);
    }

    private Runnable notifyChanges() {
        return () -> {
            if (onDataChanged != null) {
                onDataChanged.run();
            }
        };
    }

    private class FrameDragListener extends MouseAdapter {
        private Point pressPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            pressPoint = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pressPoint != null) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - pressPoint.x, current.y - pressPoint.y);
            }
        }
    }
}

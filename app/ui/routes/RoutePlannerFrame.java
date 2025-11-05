package app.ui.routes;

import app.graph.RouteGraph;
import app.model.Destination;
import app.model.RouteResult;
import app.repository.RouteRepository;
import app.ui.theme.AviationTheme;
import app.ui.utility.CustomDialog;
import app.ui.utility.OutlineLabel;
import app.ui.utility.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RoutePlannerFrame extends JFrame {

    private final RouteRepository repository;
    private final JComboBox<Destination> originCombo;
    private final JComboBox<Destination> destinationCombo;

    private List<Destination> destinations;

    public RoutePlannerFrame() {
        super("Rotas Esféricas");
        this.repository = new RouteRepository();
        this.destinations = new ArrayList<>();

        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        originCombo = buildDestinationCombo();
        destinationCombo = buildDestinationCombo();

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        reloadDestinations();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 0, 20));

        OutlineLabel title = new OutlineLabel(
            "Planejador de Rotas Aéreas",
            new Font("Arial", Font.BOLD, 34),
            Color.WHITE
        );
        header.add(title, BorderLayout.WEST);

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));

        header.add(closeButton, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 40, 20, 40));
        content.setLayout(new GridBagLayout());

        JPanel glassPanel = new JPanel();
        glassPanel.setOpaque(false);
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1, true),
            new EmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        OutlineLabel subtitle = new OutlineLabel(
            "Escolha os destinos",
            new Font("Arial", Font.BOLD, 26),
            Color.WHITE
        );
        glassPanel.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy++;
        glassPanel.add(buildLabeledCombo("Origem", originCombo), gbc);

        gbc.gridx = 1;
        glassPanel.add(buildLabeledCombo("Destino", destinationCombo), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonRow.setOpaque(false);

        RoundButton calculateButton = new RoundButton("Calcular rota", AviationTheme.PRIMARY_DARK);
        calculateButton.setPreferredSize(new Dimension(220, 65));
        calculateButton.addActionListener(e -> calculateRoute());
        buttonRow.add(calculateButton);

        RoundButton adjustmentsButton = new RoundButton("Ajustes", AviationTheme.PRIMARY_DARK);
        adjustmentsButton.setPreferredSize(new Dimension(180, 65));
        adjustmentsButton.addActionListener(e -> openAdjustments());
        buttonRow.add(adjustmentsButton);

        glassPanel.add(buttonRow, gbc);

        content.add(glassPanel);
        return content;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 25, 20, 25));

        JLabel tip = new JLabel("Dica: Ajuste suas rotas para otimizar viagens aéreas.");
        tip.setFont(new Font("Arial", Font.PLAIN, 16));
        tip.setForeground(new Color(230, 240, 252));
        footer.add(tip, BorderLayout.WEST);

        RoundButton exitButton = new RoundButton("Encerrar", AviationTheme.PRIMARY_DARK);
        exitButton.setPreferredSize(new Dimension(150, 55));
        exitButton.addActionListener(e -> System.exit(0));
        footer.add(exitButton, BorderLayout.EAST);

        return footer;
    }

    private JPanel buildLabeledCombo(String label, JComboBox<Destination> combo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        panel.add(lbl, BorderLayout.NORTH);
        combo.setFont(new Font("Arial", Font.PLAIN, 20));
        combo.setForeground(AviationTheme.PRIMARY_DARK);
        combo.setBackground(AviationTheme.SURFACE);
        combo.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        combo.setRenderer(new DestinationListCellRenderer());
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private JComboBox<Destination> buildDestinationCombo() {
        JComboBox<Destination> combo = new JComboBox<>();
        combo.setFocusable(false);
        combo.putClientProperty("JComponent.sizeVariant", "large");
        combo.setPreferredSize(new Dimension(260, 60));
        return combo;
    }

    private void reloadDestinations() {
        this.destinations = repository.loadDestinations();
        DefaultComboBoxModel<Destination> originModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Destination> destinationModel = new DefaultComboBoxModel<>();
        for (Destination destination : destinations) {
            originModel.addElement(destination);
            destinationModel.addElement(destination);
        }
        originCombo.setModel(originModel);
        destinationCombo.setModel(destinationModel);

        boolean enable = destinations.size() >= 2;
        originCombo.setEnabled(enable);
        destinationCombo.setEnabled(enable);
    }

    private void calculateRoute() {
        Destination origin = (Destination) originCombo.getSelectedItem();
        Destination destination = (Destination) destinationCombo.getSelectedItem();

        if (origin == null || destination == null) {
            CustomDialog.showMessage(this, "Cadastre ao menos dois destinos.", "Dados insuficientes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origin.getId().equals(destination.getId())) {
            CustomDialog.showMessage(this, "Escolha origem e destino diferentes.", "Seleção inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RouteGraph graph = new RouteGraph(destinations, repository.loadConnections());
        RouteResult routeResult = graph.findShortestPath(origin.getId(), destination.getId());
        if (!routeResult.hasPath()) {
            CustomDialog.showMessage(this, "Nenhuma rota encontrada entre os destinos selecionados.", "Rota inexistente", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder builder = new StringBuilder();
        List<Destination> path = routeResult.getPath();
        for (int i = 0; i < path.size(); i++) {
            builder.append(path.get(i).getName());
            if (i + 1 < path.size()) {
                builder.append(" ➜ ");
            }
        }

        RouteResultDialog dialog = new RouteResultDialog(
            this,
            builder.toString(),
            routeResult.getTotalDistance()
        );
        dialog.setVisible(true);
    }

    private void openAdjustments() {
        AdjustmentsDialog dialog = new AdjustmentsDialog(this, repository, this::reloadDestinations);
        dialog.setVisible(true);
    }

    private class FrameDragListener extends MouseAdapter {
        private Point mouseDown;

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDown = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDown != null) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - mouseDown.x, current.y - mouseDown.y);
            }
        }
    }
}

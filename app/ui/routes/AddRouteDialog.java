package app.ui.routes;

import app.model.Destination;
import app.repository.RouteRepository;
import app.ui.theme.AviationTheme;
import app.ui.utility.CustomDialog;
import app.ui.utility.OutlineLabel;
import app.ui.utility.RoundButton;
import app.ui.utility.RoundTextF;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AddRouteDialog extends JDialog {

    private final RouteRepository repository;
    private final Runnable onSaved;

    private final JComboBox<Destination> originCombo = new JComboBox<>();
    private final JComboBox<Destination> destinationCombo = new JComboBox<>();
    private final RoundTextF distanceField = new RoundTextF();

    public AddRouteDialog(Window owner, RouteRepository repository, Runnable onSaved) {
        super(owner, "Adicionar Rota", ModalityType.APPLICATION_MODAL);
        this.repository = repository;
        this.onSaved = onSaved;

        setUndecorated(true);
        setSize(540, 500);
        setLocationRelativeTo(owner);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        loadDestinations();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        OutlineLabel title = new OutlineLabel(
            "Adicionar rota manualmente",
            new Font("Arial", Font.BOLD, 28),
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

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 40, 10, 40));
        form.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        configureCombo(originCombo);
        configureCombo(destinationCombo);

        form.add(buildLabeledComponent("Destino de origem", originCombo), gbc);

        gbc.gridy++;
        form.add(buildLabeledComponent("Destino final", destinationCombo), gbc);

        gbc.gridy++;
        distanceField.setFont(new Font("Arial", Font.PLAIN, 20));
        form.add(buildLabeledComponent("Distância (km)", distanceField), gbc);

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 20, 20, 20));

        RoundButton cancel = new RoundButton("Cancelar", AviationTheme.PRIMARY_DARK);
        cancel.setPreferredSize(new Dimension(150, 55));
        cancel.addActionListener(e -> dispose());

        RoundButton save = new RoundButton("Salvar rota", AviationTheme.PRIMARY_DARK);
        save.setPreferredSize(new Dimension(170, 55));
        save.addActionListener(e -> saveRoute());

        footer.add(cancel);
        footer.add(save);
        return footer;
    }

    private JPanel buildLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        panel.add(lbl, BorderLayout.NORTH);

        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void configureCombo(JComboBox<Destination> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 20));
        combo.setBackground(AviationTheme.SURFACE);
        combo.setForeground(AviationTheme.PRIMARY_DARK);
        combo.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        combo.setRenderer(new DestinationListCellRenderer());
        combo.setFocusable(false);
    }

    private void loadDestinations() {
        List<Destination> destinations = repository.loadDestinations();
        DefaultComboBoxModel<Destination> originModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Destination> destinationModel = new DefaultComboBoxModel<>();
        for (Destination destination : destinations) {
            originModel.addElement(destination);
            destinationModel.addElement(destination);
        }
        originCombo.setModel(originModel);
        destinationCombo.setModel(destinationModel);
    }

    private void saveRoute() {
        Destination origin = (Destination) originCombo.getSelectedItem();
        Destination destination = (Destination) destinationCombo.getSelectedItem();

        if (origin == null || destination == null) {
            CustomDialog.showMessage(this, "Cadastre destinos antes de criar rotas.", "Destinos faltando", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origin.getId().equals(destination.getId())) {
            CustomDialog.showMessage(this, "Origem e destino precisam ser diferentes.", "Seleção inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String distanceText = distanceField.getText().trim();
        if (distanceText.isEmpty()) {
            CustomDialog.showMessage(this, "Informe a distância em quilômetros.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double distance;
        try {
            distance = Double.parseDouble(distanceText.replace(",", "."));
        } catch (NumberFormatException ex) {
            CustomDialog.showMessage(this, "Distância inválida. Use apenas números.", "Valor inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (distance <= 0) {
            CustomDialog.showMessage(this, "A distância deve ser maior que zero.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double existing = repository.existingRouteDistance(origin.getId(), destination.getId());
        if (existing != null) {
            CustomDialog.showMessage(
                this,
                String.format("Já existe uma rota cadastrada entre esses destinos (%.2f km).", existing),
                "Rota duplicada",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean saved = repository.addRoute(origin.getId(), destination.getId(), distance);
        if (saved) {
            CustomDialog.showMessage(this, "Rota cadastrada com sucesso!", "Rota salva", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        } else {
            CustomDialog.showMessage(this, "Não foi possível salvar a rota.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class FrameDragListener extends MouseAdapter {
        private Point point;

        @Override
        public void mousePressed(MouseEvent e) {
            point = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (point != null) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - point.x, current.y - point.y);
            }
        }
    }
}

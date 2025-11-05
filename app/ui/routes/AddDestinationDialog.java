package app.ui.routes;

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

public class AddDestinationDialog extends JDialog {

    private final RouteRepository repository;
    private final Runnable onSaved;

    private final RoundTextF nameField = new RoundTextF();
    private final RoundTextF latitudeField = new RoundTextF();
    private final RoundTextF longitudeField = new RoundTextF();

    public AddDestinationDialog(Window owner, RouteRepository repository, Runnable onSaved) {
        super(owner, "Adicionar Destino", ModalityType.APPLICATION_MODAL);
        this.repository = repository;
        this.onSaved = onSaved;

        setUndecorated(true);
        setSize(520, 480);
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
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        OutlineLabel title = new OutlineLabel(
            "Adicionar novo destino",
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
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        form.add(buildLabeledField("Nome do destino", nameField), gbc);

        gbc.gridy++;
        form.add(buildLabeledField("Latitude ( -90 a 90 )", latitudeField), gbc);

        gbc.gridy++;
        form.add(buildLabeledField("Longitude ( -180 a 180 )", longitudeField), gbc);

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 20, 20, 20));

        RoundButton cancel = new RoundButton("Cancelar", AviationTheme.PRIMARY_DARK);
        cancel.setPreferredSize(new Dimension(150, 55));
        cancel.addActionListener(e -> dispose());

        RoundButton save = new RoundButton("Salvar", AviationTheme.PRIMARY_DARK);
        save.setPreferredSize(new Dimension(150, 55));
        save.addActionListener(e -> saveDestination());

        footer.add(cancel);
        footer.add(save);
        return footer;
    }

    private JPanel buildLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        panel.add(lbl, BorderLayout.NORTH);

        field.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void saveDestination() {
        String name = nameField.getText().trim();
        String latText = latitudeField.getText().trim();
        String lonText = longitudeField.getText().trim();

        if (name.isEmpty() || latText.isEmpty() || lonText.isEmpty()) {
            CustomDialog.showMessage(this, "Preencha todos os campos.", "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (repository.destinationExistsByName(name)) {
            CustomDialog.showMessage(this, "Já existe um destino com esse nome.", "Destino duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double latitude = parseCoordinate(latText, -90, 90);
        Double longitude = parseCoordinate(lonText, -180, 180);
        if (latitude == null || longitude == null) {
            CustomDialog.showMessage(this, "Latitudes vão de -90 a 90 e longitudes de -180 a 180.", "Coordenadas inválidas", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean saved = repository.addDestination(name, latitude, longitude);
        if (saved) {
            CustomDialog.showMessage(this, "Destino cadastrado com sucesso!", "Destino salvo", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        } else {
            CustomDialog.showMessage(this, "Não foi possível salvar o destino.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double parseCoordinate(String value, double min, double max) {
        try {
            double parsed = Double.parseDouble(value.replace(",", "."));
            if (parsed < min || parsed > max) {
                return null;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return null;
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

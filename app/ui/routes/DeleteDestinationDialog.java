package app.ui.routes;

import app.model.Destination;
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
import java.util.List;

public class DeleteDestinationDialog extends JDialog {

    private final RouteRepository repository;
    private final Runnable onDeleted;
    private final DefaultListModel<Destination> listModel = new DefaultListModel<>();
    private final JList<Destination> destinationList = new JList<>(listModel);

    public DeleteDestinationDialog(Window owner, RouteRepository repository, Runnable onDeleted) {
        super(owner, "Excluir Destino", ModalityType.APPLICATION_MODAL);
        this.repository = repository;
        this.onDeleted = onDeleted;

        setUndecorated(true);
        setSize(480, 420);
        setLocationRelativeTo(owner);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildListPanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        loadDestinations();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        OutlineLabel title = new OutlineLabel(
            "Remover destino",
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

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel hint = new JLabel("Selecione o destino que deseja remover (rotas relacionadas serão removidas).");
        hint.setFont(new Font("Arial", Font.PLAIN, 15));
        hint.setForeground(new Color(230, 240, 252));
        panel.add(hint, BorderLayout.NORTH);

        destinationList.setCellRenderer(new DestinationListCellRenderer());
        destinationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        destinationList.setVisibleRowCount(8);

        JScrollPane scroll = new JScrollPane(destinationList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 250), 1, true));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 20, 18, 20));

        RoundButton cancel = new RoundButton("Cancelar", AviationTheme.PRIMARY_DARK);
        cancel.setPreferredSize(new Dimension(140, 55));
        cancel.addActionListener(e -> dispose());

        RoundButton delete = new RoundButton("Remover", AviationTheme.PRIMARY_DARK);
        delete.setPreferredSize(new Dimension(150, 55));
        delete.addActionListener(e -> deleteSelectedDestination());

        footer.add(cancel);
        footer.add(delete);
        return footer;
    }

    private void loadDestinations() {
        listModel.clear();
        List<Destination> destinations = repository.loadDestinations();
        destinations.forEach(listModel::addElement);
    }

    private void deleteSelectedDestination() {
        Destination selected = destinationList.getSelectedValue();
        if (selected == null) {
            CustomDialog.showMessage(this, "Selecione um destino para remover.", "Seleção requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente remover \"" + selected.getName() + "\"?\nAs rotas relacionadas serão excluídas.",
            "Confirmar remoção",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = repository.deleteDestination(selected.getId());
        if (removed) {
            CustomDialog.showMessage(this, "Destino removido com sucesso.", "Destino removido", JOptionPane.INFORMATION_MESSAGE);
            loadDestinations();
            if (onDeleted != null) onDeleted.run();
        } else {
            CustomDialog.showMessage(this, "Não foi possível remover o destino.", "Erro", JOptionPane.ERROR_MESSAGE);
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

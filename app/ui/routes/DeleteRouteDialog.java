package app.ui.routes;

import app.model.Destination;
import app.model.RouteListing;
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

public class DeleteRouteDialog extends JDialog {

    private final RouteRepository repository;
    private final Runnable onDeleted;

    private final DefaultListModel<Destination> originModel = new DefaultListModel<>();
    private final DefaultListModel<RouteListing> routeModel = new DefaultListModel<>();

    private final JList<Destination> originList = new JList<>(originModel);
    private final JList<RouteListing> routesList = new JList<>(routeModel);

    public DeleteRouteDialog(Window owner, RouteRepository repository, Runnable onDeleted) {
        super(owner, "Excluir Rota", ModalityType.APPLICATION_MODAL);
        this.repository = repository;
        this.onDeleted = onDeleted;

        setUndecorated(true);
        setSize(640, 450);
        setLocationRelativeTo(owner);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout());
        setContentPane(root);

        FrameDragListener dragListener = new FrameDragListener();
        root.addMouseListener(dragListener);
        root.addMouseMotionListener(dragListener);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        loadOrigins();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 25, 0, 25));

        OutlineLabel title = new OutlineLabel(
            "Remover rota",
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

    private JPanel buildContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 25, 10, 25));

        originList.setCellRenderer(new DestinationListCellRenderer());
        originList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        originList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRoutesForSelectedOrigin();
            }
        });

        routesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routesList.setFont(new Font("Arial", Font.PLAIN, 18));
        routesList.setBackground(new Color(245, 248, 255));
        routesList.setForeground(new Color(10, 34, 82));
        routesList.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 250), 1, true));

        JPanel originPanel = createListPanel("Destinos de origem", originList);
        JPanel routesPanel = createListPanel("Rotas cadastradas", routesList);

        panel.add(originPanel);
        panel.add(routesPanel);
        return panel;
    }

    private JPanel createListPanel(String title, JList<?> list) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        panel.add(lbl, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 250), 1, true));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 25, 18, 25));

        RoundButton cancel = new RoundButton("Cancelar", AviationTheme.PRIMARY_DARK);
        cancel.setPreferredSize(new Dimension(150, 55));
        cancel.addActionListener(e -> dispose());

        RoundButton delete = new RoundButton("Remover rota", AviationTheme.PRIMARY_DARK);
        delete.setPreferredSize(new Dimension(180, 55));
        delete.addActionListener(e -> deleteSelectedRoute());

        footer.add(cancel);
        footer.add(delete);
        return footer;
    }

    private void loadOrigins() {
        originModel.clear();
        List<Destination> destinations = repository.loadDestinations();
        destinations.forEach(originModel::addElement);
        if (!destinations.isEmpty()) {
            originList.setSelectedIndex(0);
        } else {
            routeModel.clear();
        }
    }

    private void loadRoutesForSelectedOrigin() {
        routeModel.clear();
        Destination origin = originList.getSelectedValue();
        if (origin == null) {
            return;
        }
        List<RouteListing> routes = repository.loadRoutesFrom(origin.getId());
        routes.forEach(routeModel::addElement);
    }

    private void deleteSelectedRoute() {
        RouteListing selectedRoute = routesList.getSelectedValue();
        Destination origin = originList.getSelectedValue();

        if (origin == null) {
            CustomDialog.showMessage(this, "Selecione um destino de origem.", "Origem obrigatória", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRoute == null) {
            CustomDialog.showMessage(this, "Selecione a rota que deseja remover.", "Seleção necessária", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente remover a rota de \"" + origin.getName() + "\" para \"" +
                selectedRoute.getDestinationName() + "\"?",
            "Confirmar remoção",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = repository.deleteRoute(selectedRoute.getId());
        if (removed) {
            CustomDialog.showMessage(this, "Rota removida com sucesso.", "Rota removida", JOptionPane.INFORMATION_MESSAGE);
            loadRoutesForSelectedOrigin();
            if (onDeleted != null) onDeleted.run();
        } else {
            CustomDialog.showMessage(this, "Não foi possível remover a rota.", "Erro", JOptionPane.ERROR_MESSAGE);
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

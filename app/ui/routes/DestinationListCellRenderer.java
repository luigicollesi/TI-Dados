package app.ui.routes;

import javax.swing.*;
import java.awt.*;

public class DestinationListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (component instanceof JLabel label) {
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            if (isSelected) {
                label.setBackground(new Color(180, 210, 255));
                label.setForeground(new Color(10, 45, 100));
            } else {
                label.setBackground(new Color(245, 248, 255));
                label.setForeground(new Color(10, 34, 82));
            }
        }
        return component;
    }
}

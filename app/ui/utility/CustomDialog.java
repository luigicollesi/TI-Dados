package app.ui.utility;

import app.ui.theme.AviationTheme;

import javax.swing.*;
import java.awt.*;

public class CustomDialog {

    public static void showMessage(Component parent, String message, String title, int messageType) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(messageType == JOptionPane.INFORMATION_MESSAGE ? 480 : 520, 240);
        dialog.setLocationRelativeTo(parent);

        JPanel root = AviationTheme.gradientPanel();
        root.setBorder(BorderFactory.createLineBorder(AviationTheme.PRIMARY_LIGHT, 2, true));
        root.setLayout(new BorderLayout(20, 20));
        dialog.setContentPane(root);

        OutlineLabel header = new OutlineLabel(
            title,
            new Font("Arial", Font.BOLD, 26),
            AviationTheme.TEXT_ON_PRIMARY
        );
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel iconLabel = new JLabel(resolveIcon(messageType), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 46));
        iconLabel.setForeground(resolveAccentColor(messageType));
        center.add(iconLabel, BorderLayout.NORTH);

        String formattedMessage = message.trim();
        if (!formattedMessage.toLowerCase().startsWith("<html")) {
            formattedMessage = "<html><center>" + formattedMessage.replace("\n", "<br>") + "</center></html>";
        }

        JLabel messageLabel = new JLabel(formattedMessage, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        messageLabel.setForeground(new Color(240, 248, 255));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        center.add(messageLabel, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        RoundButton okButton = new RoundButton("OK", AviationTheme.PRIMARY_DARK);
        okButton.setPreferredSize(new Dimension(150, 55));
        okButton.addActionListener(e -> dialog.dispose());
        footer.add(okButton);

        root.add(footer, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(okButton);
        dialog.setVisible(true);
    }

    private static String resolveIcon(int messageType) {
        return switch (messageType) {
            case JOptionPane.ERROR_MESSAGE -> "✖";
            case JOptionPane.WARNING_MESSAGE -> "⚠";
            case JOptionPane.INFORMATION_MESSAGE -> "✈";
            default -> "ℹ";
        };
    }

    private static Color resolveAccentColor(int messageType) {
        return switch (messageType) {
            case JOptionPane.ERROR_MESSAGE -> new Color(255, 99, 132);
            case JOptionPane.WARNING_MESSAGE -> new Color(255, 193, 7);
            default -> AviationTheme.PRIMARY_LIGHT;
        };
    }

    private CustomDialog() {
        throw new UnsupportedOperationException("Utility class");
    }
}

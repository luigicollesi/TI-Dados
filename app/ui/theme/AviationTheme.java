package app.ui.theme;

import javax.swing.*;
import java.awt.*;

public final class AviationTheme {

    public static final Color PRIMARY_DARK = new Color(10, 34, 82);
    public static final Color PRIMARY = new Color(20, 80, 160);
    public static final Color PRIMARY_LIGHT = new Color(120, 180, 255);
    public static final Color SURFACE = new Color(240, 247, 255, 200);
    public static final Color TEXT_ON_PRIMARY = new Color(240, 250, 255);

    private AviationTheme() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static JPanel gradientPanel() {
        return new GradientPanel();
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint base = new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), getHeight(), PRIMARY);
            g2.setPaint(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            GradientPaint overlay = new GradientPaint(
                0, getHeight(), new Color(255, 255, 255, 120),
                getWidth(), 0, new Color(255, 255, 255, 20)
            );
            g2.setPaint(overlay);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
        }
    }
}

package app.ui.utility;

import javax.swing.*;
import java.awt.*;

public class RoundButton extends JButton {
    private final Color color;

    public RoundButton(String text, Color color) {
        super(text);
        this.color = color;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("Arial", Font.BOLD, 28));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g.create();
        
        boolean hover = getModel().isRollover();

        Color backgroundColor = hover
            ? new Color(255, 255, 255, 250) // Hover
            : new Color(255, 255, 255, 200); // Normal

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

        // --- Texto centralizado ---
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics(getFont());
        int x = ( getWidth() - fm.stringWidth(getText()) ) / 2;
        int y = ( getHeight() - fm.getHeight() ) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
    }

    public Color getColor() {
        return color;
    }

}

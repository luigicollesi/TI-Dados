package app.ui.utility;

import javax.swing.*;
import java.awt.*;

public class OutlineLabel extends JLabel {

    public OutlineLabel(String text, Font font, Color textColor) {
        super(text);
        setFont(font);
        setForeground(textColor);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(getFont());
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();

        // contorno preto
        g2.setColor(Color.BLACK);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    g2.drawString(getText(), x + dx, y + dy);
                }
            }
        }

        // texto branco (ou cor definida) no centro
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
    
}

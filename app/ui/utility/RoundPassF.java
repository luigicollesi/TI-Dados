package app.ui.utility;
import javax.swing.*;
import java.awt.*;

public class RoundPassF extends JPasswordField {

    public RoundPassF() {
        setOpaque(false);
        setFont(new Font("Arial", Font.PLAIN, 26));
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }
    
}

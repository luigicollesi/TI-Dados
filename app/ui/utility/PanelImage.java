package app.ui.utility;

import java.awt.*;
import javax.swing.*;

public class PanelImage extends JPanel {
    private final Image image;
    private final boolean isDark;

    public PanelImage(Image image, boolean isDark) {
        this.image = image;
        this.isDark = isDark;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            if (isDark) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            } else {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public Image getImage() {
        return image;
    }
    
    public boolean isDark() {
        return isDark;
    }
    
}

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

// Handles HUD for the player
public abstract class HUD extends GameObject {

    private Rectangle2D square;
    ControlPanel control;
    static Font font;
    static BufferedImage spaceBackground;
    static final int DISPLAY_SCALE = 7;
    static final int BORDER_WIDTH = 2;

    static {
        URL spaceBackgroundResource = HUD.class.getResource("/Resources/Space_Background.png");
        try {
            spaceBackground = ImageIO.read(new File(spaceBackgroundResource.toURI()));
            font = Font.createFont(Font.TRUETYPE_FONT, ControlPanel.getFontFile()).deriveFont(50f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HUD(ControlPanel control) {
        super(0, 0, 1, 1, new Color(0,0,0,0));
        square = new Rectangle2D.Double(0, 0, 1, 1);
        this.control = control;
    }

    public Rectangle2D getObj() {
        return square;
    }

    public abstract void update(ControlPanel panel);

    public abstract void paintComponent(Graphics2D g2);

    public void drawCenteredString(Graphics g, Rectangle r, String s,
                             Font font) {
        FontRenderContext frc =
                new FontRenderContext(null, true, true);
        String[] lines = s.split("\n");
        FontMetrics metrics = g.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        int maxLineWidth = metrics.stringWidth(lines[0]);
        for (int i = 0; i < lines.length; i++) {
            if (metrics.stringWidth(lines[i]) > maxLineWidth) {
                maxLineWidth = metrics.stringWidth(lines[i]);
            }
        }
        g.setFont(font);
        for (int i = 0; i < lines.length; i++) {
            Rectangle2D r2D = font.getStringBounds(lines[i], frc);
            int rWidth = (int) Math.round(r2D.getWidth());
            int rHeight = (int) Math.round(r2D.getHeight());
            int rX = (int) Math.round(r2D.getX());
            int rY = (int) Math.round(r2D.getY());

            int a = (r.width / 2) - (rWidth / 2) - rX;
            int b = (r.height / 2) - (rHeight / 2) - rY;
            drawBorderedString(g, lines[i], r.x + a, r.y + b - lines.length / 2 * lineHeight + i * lineHeight, maxLineWidth, lineHeight);
        }
    }

    public void drawBorderedString(Graphics g, String s, int x, int y, int width, int height) {
        /*
        g.setColor(ControlPanel.TEXT_BACKGROUND);
        g.fill3DRect(x - 15, y - height, width + 30, height + 35, false);
        */
        g.setColor(ControlPanel.TEXT_BORDER);
        g.drawString(s, x + BORDER_WIDTH, y + BORDER_WIDTH);
        g.drawString(s, x + BORDER_WIDTH, y - BORDER_WIDTH);
        g.drawString(s, x - BORDER_WIDTH, y + BORDER_WIDTH);
        g.drawString(s, x - BORDER_WIDTH, y - BORDER_WIDTH);
        g.setColor(ControlPanel.TEXT);
        g.drawString(s, x, y);
    }
}
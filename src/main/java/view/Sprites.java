package view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Loads the game's images from {@code src/main/resources/sprites} and draws them rotated.
 * A missing image loads as {@code null}, so a painter can fall back to plain shapes.
 */
final class Sprites {
    private Sprites() {
    }

    /**
     * @param name file name inside the sprites folder, e.g. "tank-blue.png"
     * @return the image, or null if it is missing or can't be read
     */
    static BufferedImage load(String name) {
        final URL url = Sprites.class.getResource("/sprites/" + name);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        }
        catch (IOException e) {
            return null;
        }
    }

    /**
     * Draws a square image centred on (x, y), scaled to {@code size} pixels and turned by {@code angle}
     * radians (clockwise, like the game's angles).
     */
    static void drawCentred(Graphics2D g, BufferedImage image, double x, double y, double size, double angle) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.translate(x, y);
        g2.rotate(angle);
        g2.scale(size / image.getWidth(), size / image.getHeight());
        g2.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
        g2.dispose();
    }
}

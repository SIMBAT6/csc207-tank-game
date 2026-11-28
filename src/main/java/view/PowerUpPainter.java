package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interface_adapter.collect_power_up.PowerUpState;
import interface_adapter.collect_power_up.PowerUpViewModel;

/**
 * Draws Collect Power-Up (UC4) on the game screen, from the power-up view model's state:
 * {@link #paintItems} on the ITEMS layer (power-ups lying on the map and the laser aim line) and
 * {@link #paintStatus} on the OVERLAY layer (each player's active power-up).
 */
public class PowerUpPainter {
    private static final int SIZE = 20;
    /** Icon images are drawn this big; the round badge inside fills about 80% of it. */
    private static final int ICON_SIZE = 28;
    private static final List<String> LABELS = List.of("Shotgun", "Cannon", "Laser", "Shield", "Shrink");
    private static final int MARGIN = 10;
    private static final Stroke DASHED = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10f, new float[] {6f, 6f}, 0f);
    private static final Color AIM_COLOUR = new Color(255, 60, 60, 170);

    private final PowerUpViewModel viewModel;
    /** Icon image for each power-up label, loaded from sprites/powerup-<label>.png. */
    private final Map<String, BufferedImage> icons = new HashMap<>();

    public PowerUpPainter(PowerUpViewModel viewModel) {
        this.viewModel = viewModel;
        for (String label : LABELS) {
            final BufferedImage image = Sprites.load("powerup-" + label.toLowerCase() + ".png");
            if (image != null) {
                icons.put(label, image);
            }
        }
    }

    /**
     * Draws the laser aim lines, then each power-up on the map as its icon image, or as a coloured
     * circle with its first letter if the image is missing.
     */
    public void paintItems(Graphics2D g) {
        paintAimLines(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        for (PowerUpState.Icon icon : viewModel.getState().getIcons()) {
            final BufferedImage image = icons.get(icon.getLabel());
            if (image != null) {
                Sprites.drawCentred(g, image, icon.getX(), icon.getY(), ICON_SIZE, 0);
                continue;
            }
            final int left = (int) icon.getX() - SIZE / 2;
            final int top = (int) icon.getY() - SIZE / 2;
            g.setColor(colourFor(icon.getLabel()));
            g.fillOval(left, top, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval(left, top, SIZE, SIZE);
            g.drawString(icon.getLabel().substring(0, 1), left + 6, top + 15);
        }
    }

    /** Dashed line along where a held laser would bounce. */
    private void paintAimLines(Graphics2D g) {
        final Stroke oldStroke = g.getStroke();
        g.setStroke(DASHED);
        g.setColor(AIM_COLOUR);
        for (List<double[]> path : viewModel.getState().getAimPaths()) {
            for (int i = 1; i < path.size(); i++) {
                final double[] from = path.get(i - 1);
                final double[] to = path.get(i);
                g.drawLine((int) from[0], (int) from[1], (int) to[0], (int) to[1]);
            }
        }
        g.setStroke(oldStroke);
    }

    /** Shows "P1: Shotgun (8s)" at the top left and Player 2's power-up at the top right. */
    public void paintStatus(Graphics2D g) {
        final List<String> labels = viewModel.getState().getHudLabels();
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).isEmpty()) {
                continue;
            }
            final String text = "P" + (i + 1) + ": " + labels.get(i);
            final int width = g.getFontMetrics().stringWidth(text);
            final int x = i == 0 ? MARGIN : GameView.WIDTH - MARGIN - width;
            g.setColor(colourFor(labels.get(i).split(" ")[0]));
            g.drawString(text, x, MARGIN + 14);
        }
    }

    private static Color colourFor(String label) {
        switch (label) {
            case "Shotgun":
                return Color.ORANGE;
            case "Cannon":
                return Color.GRAY;
            case "Shield":
                return Color.CYAN;
            case "Shrink":
                return Color.MAGENTA;
            case "Laser":
                return Color.RED;
            default:
                return Color.LIGHT_GRAY;
        }
    }
}

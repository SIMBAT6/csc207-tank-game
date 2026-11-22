package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import interface_adapter.collect_power_up.PowerUpState;
import interface_adapter.collect_power_up.PowerUpViewModel;

/**
 * Draws Collect Power-Up (UC4) on the game screen, from the power-up view model's state:
 * {@link #paintItems} on the ITEMS layer (power-ups lying on the map) and
 * {@link #paintStatus} on the OVERLAY layer (each player's active power-up).
 */
public class PowerUpPainter {
    private static final int SIZE = 20;
    private static final int MARGIN = 10;

    private final PowerUpViewModel viewModel;

    public PowerUpPainter(PowerUpViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /** Draws each power-up on the map as a coloured circle with its first letter. */
    public void paintItems(Graphics2D g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        for (PowerUpState.Icon icon : viewModel.getState().getIcons()) {
            final int left = (int) icon.getX() - SIZE / 2;
            final int top = (int) icon.getY() - SIZE / 2;
            g.setColor(colourFor(icon.getLabel()));
            g.fillOval(left, top, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval(left, top, SIZE, SIZE);
            g.drawString(icon.getLabel().substring(0, 1), left + 6, top + 15);
        }
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
            default:
                return Color.LIGHT_GRAY;
        }
    }
}

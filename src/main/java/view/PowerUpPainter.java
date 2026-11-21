package view;

import java.awt.Color;
import java.awt.Graphics2D;

import interface_adapter.collect_power_up.PowerUpState;

/**
 * Draws power-up icons on the game screen. The game panel (UC3) calls
 * {@code paint(g, powerUpViewModel.getState())} inside its paintComponent.
 */
public class PowerUpPainter {

    private static final int SIZE = 16;

    public void paint(Graphics2D g, PowerUpState state) {
        for (PowerUpState.Icon icon : state.getIcons()) {
            final int left = (int) icon.getX() - SIZE / 2;
            final int top = (int) icon.getY() - SIZE / 2;
            g.setColor(colourFor(icon.getLabel()));
            g.fillOval(left, top, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawString(icon.getLabel().substring(0, 1), left + SIZE / 2 - 3, top + SIZE - 4);
        }
    }

    private Color colourFor(String label) {
        switch (label) {
            case "Shotgun":
                return Color.ORANGE;
            case "Laser":
                return Color.RED;
            case "Shield":
                return Color.CYAN;
            default:
                return Color.LIGHT_GRAY;
        }
    }
}

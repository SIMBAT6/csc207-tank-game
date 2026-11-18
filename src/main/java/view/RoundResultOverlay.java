package view;

import interface_adapter.end_round.EndRoundState;
import interface_adapter.end_round.EndRoundViewModel;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Draws the score bar and the round-over banner on top of the game screen (UC5).
 * After a round (not the last one) it waits {@link #ROUND_DELAY_MS} and then starts the next round.
 * The game screen calls {@link #paint(Graphics2D, int, int)} at the end of its paintComponent.
 */
public class RoundResultOverlay implements PropertyChangeListener {
    public static final int ROUND_DELAY_MS = 2500;

    private final EndRoundViewModel viewModel;
    private final Timer nextRoundTimer;
    private Runnable nextRoundAction = () -> { };
    private boolean showBanner;

    public RoundResultOverlay(EndRoundViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addPropertyChangeListener(this);
        nextRoundTimer = new Timer(ROUND_DELAY_MS, e -> {
            showBanner = false;
            nextRoundAction.run();
        });
        nextRoundTimer.setRepeats(false);
    }

    /** What to run when the delay after a round is over (Start Match's next round, UC2). */
    public void setNextRoundAction(Runnable nextRoundAction) {
        this.nextRoundAction = nextRoundAction;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final EndRoundState state = viewModel.getState();
        showBanner = state.getBanner() != null;
        if (showBanner && !state.isMatchOver()) {
            nextRoundTimer.restart();
        }
    }

    /** Draws the score bar and, after a round, the banner. */
    public void paint(Graphics2D g, int width, int height) {
        final EndRoundState state = viewModel.getState();

        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.setColor(Color.DARK_GRAY);
        drawCentered(g, "P1  " + state.getScore1() + " : " + state.getScore2() + "  P2", width, 24);

        if (!showBanner) {
            return;
        }
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, height / 2 - 60, width, 120);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 32f));
        drawCentered(g, state.getBanner(), width, height / 2);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        if (state.getWarning() != null) {
            g.setColor(new Color(255, 200, 80));
            drawCentered(g, state.getWarning(), width, height / 2 + 28);
        }
        if (state.isMatchOver()) {
            g.setColor(Color.LIGHT_GRAY);
            drawCentered(g, "Press Esc to return to the menu", width, height / 2 + 48);
        }
    }

    private static void drawCentered(Graphics2D g, String text, int width, int baselineY) {
        final FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, (width - metrics.stringWidth(text)) / 2, baselineY);
    }
}

package view;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * The in-game screen. Runs the game loop with a Swing Timer: every tick it calls the
 * tick action (game logic lives in the use case layer) and then repaints.
 * This class should only draw and collect input — no game rules here.
 */
public class GameView extends JPanel {
    public static final String VIEW_NAME = "game";
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int TICK_MS = 16;

    private final Timer timer;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private Runnable tickAction = () -> { };
    private Runnable backAction = () -> { };
    private long frameCount;

    public GameView() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(230, 230, 230));
        setFocusable(true);

        timer = new Timer(TICK_MS, e -> {
            frameCount++;
            tickAction.run();
            repaint();
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    backAction.run();
                    return;
                }
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
    }

    /** Starts the game loop and grabs keyboard focus. */
    public void start() {
        frameCount = 0;
        pressedKeys.clear();
        timer.start();
        requestFocusInWindow();
    }

    public void stop() {
        timer.stop();
    }

    /** TODO (UC3 lead): call the ControlTankController here, passing getPressedKeys(). */
    public void setTickAction(Runnable tickAction) {
        this.tickAction = tickAction;
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }

    /** Key codes currently held down, for building the tick's input data. */
    public Set<Integer> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // TODO (UC2/UC3/UC4 leads): draw the maze, tanks, bullets and power-ups from the GameViewModel.
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Game loop running - frame " + frameCount, 20, 30);
        g2.drawString("Keys held: " + pressedKeys, 20, 50);
        g2.drawString("Press Esc to return to the menu", 20, 70);
    }
}

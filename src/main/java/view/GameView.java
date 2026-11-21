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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The in-game screen. It owns the game loop but no game rules: each use case plugs in
 * through AppBuilder with
 * <ul>
 *   <li>{@link #addTickAction(Runnable)} — work to do every tick, usually calling its controller;</li>
 *   <li>{@link #addPainter(GameLayer, GameLayerPainter)} — how to draw its part of the screen.</li>
 * </ul>
 * Every tick runs all tick actions in the order they were added, then repaints.
 */
public class GameView extends JPanel {
    public static final String VIEW_NAME = "game";
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int TICK_MS = 16;

    private final Timer timer = new Timer(TICK_MS, e -> tick());
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final List<Runnable> tickActions = new ArrayList<>();
    private final Map<GameLayer, List<GameLayerPainter>> painters = new EnumMap<>(GameLayer.class);
    private Runnable backAction = () -> { };
    private long tickCount;

    public GameView() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(230, 230, 230));
        setFocusable(true);
        for (GameLayer layer : GameLayer.values()) {
            painters.put(layer, new ArrayList<>());
        }

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

    /** Starts the game loop from tick 0 and grabs keyboard focus. */
    public void start() {
        tickCount = 0;
        pressedKeys.clear();
        timer.start();
        requestFocusInWindow();
    }

    public void stop() {
        timer.stop();
    }

    /** Adds work to run every tick, after the actions added before it. */
    public void addTickAction(Runnable action) {
        tickActions.add(action);
    }

    /** Adds a painter to a layer. Layers are drawn back to front: MAZE, ITEMS, TANKS, OVERLAY. */
    public void addPainter(GameLayer layer, GameLayerPainter painter) {
        painters.get(layer).add(painter);
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }

    /** Key codes currently held down, for building a tick's input data. */
    public Set<Integer> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    /** Ticks since start() was last called. */
    public long getTickCount() {
        return tickCount;
    }

    /** One step of the game loop. Called by the timer; package-private for tests. */
    void tick() {
        tickCount++;
        for (Runnable action : tickActions) {
            action.run();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintLayers(g2);
    }

    /** Runs every painter, back layer first, each on its own copy of the graphics. */
    void paintLayers(Graphics2D g) {
        for (GameLayer layer : GameLayer.values()) {
            for (GameLayerPainter painter : painters.get(layer)) {
                Graphics2D copy = (Graphics2D) g.create();
                try {
                    painter.paint(copy);
                } finally {
                    copy.dispose();
                }
            }
        }
    }
}

package view;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameViewTest {
    private final GameView view = new GameView();
    private final List<String> calls = new ArrayList<>();

    private static Graphics2D newGraphics() {
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
    }

    @Test
    void tickRunsActionsInTheOrderTheyWereAdded() {
        view.addTickAction(() -> calls.add("control tank"));
        view.addTickAction(() -> calls.add("collect power-up"));

        view.tick();

        assertEquals(List.of("control tank", "collect power-up"), calls);
    }

    @Test
    void tickCountsTicks() {
        view.tick();
        view.tick();

        assertEquals(2, view.getTickCount());
    }

    @Test
    void layersAreDrawnBackToFrontWhateverOrderTheyAreAdded() {
        view.addPainter(GameLayer.OVERLAY, g -> calls.add("overlay"));
        view.addPainter(GameLayer.TANKS, g -> calls.add("tanks"));
        view.addPainter(GameLayer.MAZE, g -> calls.add("maze"));
        view.addPainter(GameLayer.ITEMS, g -> calls.add("items"));

        view.paintLayers(newGraphics());

        assertEquals(List.of("maze", "items", "tanks", "overlay"), calls);
    }

    @Test
    void paintersOnTheSameLayerKeepTheirOrder() {
        view.addPainter(GameLayer.OVERLAY, g -> calls.add("power-up status"));
        view.addPainter(GameLayer.OVERLAY, g -> calls.add("round banner"));

        view.paintLayers(newGraphics());

        assertEquals(List.of("power-up status", "round banner"), calls);
    }

    @Test
    void graphicsSettingsDoNotLeakBetweenPainters() {
        Graphics2D g = newGraphics();
        g.setColor(Color.BLUE);
        List<Color> seen = new ArrayList<>();
        view.addPainter(GameLayer.MAZE, painterG -> painterG.setColor(Color.RED));
        view.addPainter(GameLayer.TANKS, painterG -> seen.add(painterG.getColor()));

        view.paintLayers(g);

        assertEquals(List.of(Color.BLUE), seen);
    }
}

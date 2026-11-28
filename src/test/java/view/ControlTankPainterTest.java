package view;

import entity.Match;
import interface_adapter.control_tank.ControlTankPresenter;
import interface_adapter.control_tank.ControlTankViewModel;
import org.junit.jupiter.api.Test;
import use_case.control_tank.ControlTankOutputData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlTankPainterTest {
    private final ControlTankViewModel viewModel = new ControlTankViewModel();
    private final BufferedImage image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);

    /** Paints with plain shapes, so single pixels have known colours. */
    private void show(boolean tank2Alive, List<ControlTankOutputData.BulletData> bullets) {
        show(new ControlTankPainter(viewModel, ControlTankPainter.Skin.NONE), tank2Alive, bullets);
    }

    private void show(ControlTankPainter painter, boolean tank2Alive, List<ControlTankOutputData.BulletData> bullets) {
        new ControlTankPresenter(viewModel).prepareTickView(new ControlTankOutputData(
                List.of(new ControlTankOutputData.TankData(1, 100, 100, 0, 14, true, false),
                        new ControlTankOutputData.TankData(2, 300, 100, Math.PI, 14, tank2Alive, false)),
                bullets, Match.NO_WINNER));
        Graphics2D g = image.createGraphics();
        painter.paint(g);
        g.dispose();
    }

    private Color at(int x, int y) {
        return new Color(image.getRGB(x, y));
    }

    /** How many pixels in the box were painted (the image starts black). */
    private int paintedIn(int left, int top, int right, int bottom) {
        int count = 0;
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                if ((image.getRGB(x, y) & 0xFFFFFF) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    @Test
    void allTankSpritesAreBundled() {
        ControlTankPainter.Skin skin = ControlTankPainter.Skin.load();
        for (int player = 1; player <= 2; player++) {
            assertNotNull(skin.imageFor(player, true));
            assertNotNull(skin.imageFor(player, false));
        }
    }

    @Test
    void aTankSpriteIsLongerThanItIsWideAndTurnsWithTheTank() {
        show(new ControlTankPainter(viewModel), true, List.of());

        // Tank 1 at (100, 100) faces right: painted along x up to the barrel tip, narrower along y.
        assertTrue(paintedIn(95, 95, 105, 105) > 100);
        assertTrue(paintedIn(118, 99, 121, 101) > 0);
        assertEquals(0, paintedIn(98, 113, 102, 116));
        // Tank 2 at (300, 100) faces left: its barrel points the other way.
        assertTrue(paintedIn(279, 99, 282, 101) > 0);
        assertEquals(0, paintedIn(318, 99, 321, 101));
    }

    @Test
    void tanksAreDrawnInTheirPlayersColours() {
        show(true, List.of());

        assertEquals(ControlTankPainter.PLAYER_1, at(95, 95));
        assertEquals(ControlTankPainter.PLAYER_2, at(305, 105));
    }

    @Test
    void aDestroyedTankIsAGreyWreck() {
        show(false, List.of());

        assertEquals(ControlTankPainter.WRECK, at(305, 105));
    }

    @Test
    void aBulletIsADotAndABeamIsALine() {
        show(true, List.of(
                new ControlTankOutputData.BulletData(1, 3, List.of(new double[] {200, 50})),
                new ControlTankOutputData.BulletData(2, 3, List.of(new double[] {20, 180}, new double[] {380, 180}))));

        assertEquals(ControlTankPainter.BULLET, at(200, 50));
        assertEquals(ControlTankPainter.PLAYER_2, at(200, 180));
    }
}

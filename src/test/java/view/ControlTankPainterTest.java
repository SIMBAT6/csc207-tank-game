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

class ControlTankPainterTest {
    private final ControlTankViewModel viewModel = new ControlTankViewModel();
    private final ControlTankPainter painter = new ControlTankPainter(viewModel);
    private final BufferedImage image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);

    private void show(boolean tank2Alive, List<ControlTankOutputData.BulletData> bullets) {
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

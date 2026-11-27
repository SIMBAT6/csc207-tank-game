package view;

import interface_adapter.control_tank.ControlTankState;
import interface_adapter.control_tank.ControlTankViewModel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Draws Control Tank (UC3) on the TANKS layer: each tank as a coloured body with a barrel,
 * a ring while shielded, a grey wreck once destroyed; bullets as dots and beams as lines.
 */
public final class ControlTankPainter implements GameLayerPainter {
    static final Color PLAYER_1 = new Color(0x6BCB77);
    static final Color PLAYER_2 = new Color(0xFF8C42);
    static final Color WRECK = new Color(0x555555);
    static final Color SHIELD = new Color(0x4DD0E1);
    static final Color BULLET = new Color(0xF2F2F2);
    private static final float BEAM_WIDTH = 3f;
    /** Same as Tank.BARREL_LENGTH_RATIO, so bullets appear at the tip of the drawn barrel. */
    private static final double BARREL_LENGTH_RATIO = 1.4;

    private final ControlTankViewModel viewModel;

    public ControlTankPainter(ControlTankViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void paint(Graphics2D g) {
        final ControlTankState state = viewModel.getState();
        for (ControlTankState.TankSprite tank : state.getTanks()) {
            paintTank(g, tank);
        }
        for (ControlTankState.BulletSprite bullet : state.getBullets()) {
            paintBullet(g, bullet);
        }
    }

    private static void paintTank(Graphics2D g, ControlTankState.TankSprite tank) {
        final double r = tank.getRadius();
        final Ellipse2D body = new Ellipse2D.Double(tank.getX() - r, tank.getY() - r, 2 * r, 2 * r);
        if (!tank.isAlive()) {
            g.setColor(WRECK);
            g.fill(body);
            return;
        }
        g.setColor(colourOf(tank.getId()));
        g.fill(body);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke((float) Math.max(2, r / 3)));
        g.draw(new Line2D.Double(tank.getX(), tank.getY(),
                tank.getX() + Math.cos(tank.getAngle()) * r * BARREL_LENGTH_RATIO,
                tank.getY() + Math.sin(tank.getAngle()) * r * BARREL_LENGTH_RATIO));
        if (tank.isShielded()) {
            g.setColor(SHIELD);
            g.setStroke(new BasicStroke(2f));
            g.draw(new Ellipse2D.Double(tank.getX() - r - 4, tank.getY() - r - 4, 2 * r + 8, 2 * r + 8));
        }
    }

    private static void paintBullet(Graphics2D g, ControlTankState.BulletSprite bullet) {
        final List<double[]> path = bullet.getPath();
        if (path.size() == 1) {
            final double r = bullet.getRadius();
            g.setColor(BULLET);
            g.fill(new Ellipse2D.Double(path.get(0)[0] - r, path.get(0)[1] - r, 2 * r, 2 * r));
            return;
        }
        final Path2D.Double beam = new Path2D.Double();
        beam.moveTo(path.get(0)[0], path.get(0)[1]);
        for (double[] point : path.subList(1, path.size())) {
            beam.lineTo(point[0], point[1]);
        }
        g.setColor(colourOf(bullet.getOwnerId()));
        g.setStroke(new BasicStroke(BEAM_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(beam);
    }

    private static Color colourOf(int playerId) {
        return playerId == 1 ? PLAYER_1 : PLAYER_2;
    }
}

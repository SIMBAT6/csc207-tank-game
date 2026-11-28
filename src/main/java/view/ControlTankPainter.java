package view;

import interface_adapter.control_tank.ControlTankState;
import interface_adapter.control_tank.ControlTankViewModel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Draws Control Tank (UC3) on the TANKS layer: each tank as its player's sprite (blue for Player 1,
 * red for Player 2) turned to face its angle, a ring while shielded, a wreck sprite once destroyed;
 * bullets as dots and beams as lines in the owner's colour. Without the sprite images it draws
 * plain shapes instead.
 */
public final class ControlTankPainter implements GameLayerPainter {
    static final Color PLAYER_1 = new Color(0x1C5EEC);
    static final Color PLAYER_2 = new Color(0xDB031A);
    static final Color WRECK = new Color(0x555555);
    static final Color SHIELD = new Color(0x4DD0E1);
    static final Color BULLET = new Color(0xF2F2F2);
    private static final float BEAM_WIDTH = 3f;
    /**
     * Same as Tank.BARREL_LENGTH_RATIO: the barrel tip is this many radii from the centre, so bullets
     * appear at the tip of the drawn barrel. The sprites are square with the tank's centre in the middle
     * and the barrel tip on the right edge, so a sprite is drawn 2 * BARREL_LENGTH_RATIO radii wide.
     */
    private static final double BARREL_LENGTH_RATIO = 1.7;
    private static final double SHIELD_RING_RATIO = 1.35;

    /** The tank images: alive and destroyed, for each player. A missing image means "draw shapes". */
    static final class Skin {
        static final Skin NONE = new Skin(null, null, null, null);

        private final BufferedImage tank1;
        private final BufferedImage tank2;
        private final BufferedImage wreck1;
        private final BufferedImage wreck2;

        Skin(BufferedImage tank1, BufferedImage tank2, BufferedImage wreck1, BufferedImage wreck2) {
            this.tank1 = tank1;
            this.tank2 = tank2;
            this.wreck1 = wreck1;
            this.wreck2 = wreck2;
        }

        /** Loads the sprites from {@code src/main/resources/sprites}. */
        static Skin load() {
            return new Skin(Sprites.load("tank-blue.png"), Sprites.load("tank-red.png"),
                    Sprites.load("wreck-blue.png"), Sprites.load("wreck-red.png"));
        }

        BufferedImage imageFor(int playerId, boolean alive) {
            if (playerId == 1) {
                return alive ? tank1 : wreck1;
            }
            return alive ? tank2 : wreck2;
        }
    }

    private final ControlTankViewModel viewModel;
    private final Skin skin;

    public ControlTankPainter(ControlTankViewModel viewModel) {
        this(viewModel, Skin.load());
    }

    ControlTankPainter(ControlTankViewModel viewModel, Skin skin) {
        this.viewModel = viewModel;
        this.skin = skin;
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

    private void paintTank(Graphics2D g, ControlTankState.TankSprite tank) {
        final double r = tank.getRadius();
        final BufferedImage image = skin.imageFor(tank.getId(), tank.isAlive());
        if (image != null) {
            Sprites.drawCentred(g, image, tank.getX(), tank.getY(), 2 * BARREL_LENGTH_RATIO * r, tank.getAngle());
        }
        else {
            paintShapes(g, tank);
        }
        if (tank.isAlive() && tank.isShielded()) {
            final double ring = r * SHIELD_RING_RATIO;
            g.setColor(SHIELD);
            g.setStroke(new BasicStroke(2f));
            g.draw(new Ellipse2D.Double(tank.getX() - ring, tank.getY() - ring, 2 * ring, 2 * ring));
        }
    }

    /** The look without sprite images: a coloured circle with a barrel, or a grey circle once destroyed. */
    private static void paintShapes(Graphics2D g, ControlTankState.TankSprite tank) {
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

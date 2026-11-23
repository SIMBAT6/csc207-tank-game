package use_case.control_tank;

import entity.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * The tanks and bullets to draw after a tick, as plain values so outer layers can't change the game.
 */
public final class ControlTankOutputData {

    /** One tank's position and look. */
    public static final class TankData {
        private final int id;
        private final double x;
        private final double y;
        private final double angle;
        private final double radius;
        private final boolean alive;
        private final boolean shielded;

        public TankData(int id, double x, double y, double angle, double radius,
                        boolean alive, boolean shielded) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.radius = radius;
            this.alive = alive;
            this.shielded = shielded;
        }

        public int getId() {
            return id;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getAngle() {
            return angle;
        }

        public double getRadius() {
            return radius;
        }

        public boolean isAlive() {
            return alive;
        }

        public boolean isShielded() {
            return shielded;
        }
    }

    /** One bullet. Its path is a single point for a normal bullet, or the bounce points of a beam. */
    public static final class BulletData {
        private final int ownerId;
        private final double radius;
        private final List<double[]> path;

        public BulletData(int ownerId, double radius, List<double[]> path) {
            this.ownerId = ownerId;
            this.radius = radius;
            this.path = copy(path);
        }

        public int getOwnerId() {
            return ownerId;
        }

        public double getRadius() {
            return radius;
        }

        /** @return points {x, y}; a copy, so changing it changes nothing here */
        public List<double[]> getPath() {
            return copy(path);
        }

        private static List<double[]> copy(List<double[]> points) {
            final List<double[]> copy = new ArrayList<>();
            for (double[] point : points) {
                copy.add(point.clone());
            }
            return copy;
        }
    }

    private final List<TankData> tanks;
    private final List<BulletData> bullets;
    private final int winner;

    /**
     * @param winner {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.DRAW} once the round is over,
     *               {@code Match.NO_WINNER} while it is still going
     */
    public ControlTankOutputData(List<TankData> tanks, List<BulletData> bullets, int winner) {
        this.tanks = new ArrayList<>(tanks);
        this.bullets = new ArrayList<>(bullets);
        this.winner = winner;
    }

    public List<TankData> getTanks() {
        return new ArrayList<>(tanks);
    }

    public List<BulletData> getBullets() {
        return new ArrayList<>(bullets);
    }

    public int getWinner() {
        return winner;
    }

    public boolean isRoundOver() {
        return winner != Match.NO_WINNER;
    }
}

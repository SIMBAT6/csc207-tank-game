package interface_adapter.control_tank;

import java.util.ArrayList;
import java.util.List;

/**
 * What the game screen shows for Control Tank: every tank and bullet, and the winner once the round is over.
 */
public final class ControlTankState {

    /** A tank as drawn: position, facing, size, and whether it is destroyed or shielded. */
    public static final class TankSprite {
        private final int id;
        private final double x;
        private final double y;
        private final double angle;
        private final double radius;
        private final boolean alive;
        private final boolean shielded;

        public TankSprite(int id, double x, double y, double angle, double radius,
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

    /** A bullet as drawn: a dot for one point, a line through the points for a beam. */
    public static final class BulletSprite {
        private final int ownerId;
        private final double radius;
        private final List<double[]> path;

        public BulletSprite(int ownerId, double radius, List<double[]> path) {
            this.ownerId = ownerId;
            this.radius = radius;
            this.path = new ArrayList<>(path);
        }

        public int getOwnerId() {
            return ownerId;
        }

        public double getRadius() {
            return radius;
        }

        public List<double[]> getPath() {
            return new ArrayList<>(path);
        }
    }

    private List<TankSprite> tanks = new ArrayList<>();
    private List<BulletSprite> bullets = new ArrayList<>();
    private boolean roundOver;
    private int winner;

    public List<TankSprite> getTanks() {
        return new ArrayList<>(tanks);
    }

    public List<BulletSprite> getBullets() {
        return new ArrayList<>(bullets);
    }

    public boolean isRoundOver() {
        return roundOver;
    }

    /** @return {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.DRAW} once the round is over */
    public int getWinner() {
        return winner;
    }

    void setTanks(List<TankSprite> tanks) {
        this.tanks = new ArrayList<>(tanks);
    }

    void setBullets(List<BulletSprite> bullets) {
        this.bullets = new ArrayList<>(bullets);
    }

    void setRoundOver(boolean roundOver, int winner) {
        this.roundOver = roundOver;
        this.winner = winner;
    }
}

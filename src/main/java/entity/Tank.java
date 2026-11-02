package entity;

/**
 * A tank controlled by one player.
 * TODO (UC3 lead): add movement, rotation, firing and collision bounds.
 * TODO (UC4 lead): add applyPowerUp(PowerUp).
 */
public class Tank {
    private final int id;
    private double x;
    private double y;
    private double angle;
    private final double speed;
    private boolean alive;
    private final int maxBullets;
    private PowerUp powerUp;

    public Tank(int id, double x, double y, double angle, double speed, int maxBullets) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.maxBullets = maxBullets;
        this.alive = true;
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

    public double getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }
}

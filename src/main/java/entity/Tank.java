package entity;

/**
 * A tank controlled by one player.
 * TODO (UC3 lead): add movement, rotation, firing and collision bounds.
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

    /** @return the power-up this tank holds, or null if it uses the normal weapon. */
    public PowerUp getPowerUp() {
        return powerUp;
    }

    /**
     * Gives this tank a power-up (UC4). Replaces any power-up it already holds.
     *
     * @param newPowerUp the collected power-up
     */
    public void applyPowerUp(PowerUp newPowerUp) {
        this.powerUp = newPowerUp;
    }

    /** Removes the held power-up so the tank goes back to its normal weapon (UC4). */
    public void clearPowerUp() {
        this.powerUp = null;
    }

    /** @return true if the held power-up currently protects this tank from hits. */
    public boolean hasShield() {
        return powerUp != null && powerUp.providesShield();
    }
}

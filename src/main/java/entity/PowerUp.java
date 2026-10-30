package entity;

/**
 * A pickup that gives a tank a special weapon or ability for a limited time.
 * TODO (UC4 lead): add subclasses (e.g. ShotgunPowerUp) and fire(Tank) / isExpired().
 */
public abstract class PowerUp {
    private final double x;
    private final double y;
    private int duration;

    protected PowerUp(double x, double y, int duration) {
        this.x = x;
        this.y = y;
        this.duration = duration;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDuration() {
        return duration;
    }
}

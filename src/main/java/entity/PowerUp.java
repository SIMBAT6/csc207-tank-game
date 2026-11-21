package entity;

/**
 * A pickup that gives a tank a special weapon or ability for a limited time (UC4).
 *
 * <p>New power-ups are added by subclassing (Open/Closed Principle): UC3 (Control Tank)
 * only talks to this abstract type when a tank fires, so it never needs to know which
 * concrete power-up the tank holds.</p>
 */
public abstract class PowerUp {

    /** Ammo value meaning "this power-up is not limited by shots". */
    public static final int UNLIMITED_AMMO = -1;

    private final double x;
    private final double y;
    private int duration;
    private final PowerUpType type;
    private int ammo;

    /**
     * @param x        x position (pixels) of the power-up's centre on the map
     * @param y        y position (pixels) of the power-up's centre on the map
     * @param duration how many game ticks the effect lasts once collected
     * @param type     which kind of power-up this is
     * @param ammo     shots before it runs out, or {@link #UNLIMITED_AMMO}
     */
    protected PowerUp(double x, double y, int duration, PowerUpType type, int ammo) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.type = type;
        this.ammo = ammo;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /** @return ticks left before this power-up expires. */
    public int getDuration() {
        return duration;
    }

    public PowerUpType getType() {
        return type;
    }

    public int getAmmo() {
        return ammo;
    }

    /** Called once per game tick while a tank holds this power-up. */
    public void tick() {
        if (duration > 0) {
            duration--;
        }
    }

    /** Called by UC3 each time the holder fires with this power-up. */
    public void consumeAmmo() {
        if (ammo > 0) {
            ammo--;
        }
    }

    /** @return true when the duration has run out or the ammo is used up. */
    public boolean isExpired() {
        return duration <= 0 || ammo == 0;
    }

    /**
     * Angles (in degrees, relative to the tank's facing direction) of the bullets fired
     * with this power-up. UC3 creates one bullet per angle. Normal weapon = {0}.
     */
    public abstract double[] getShotAngleOffsets();

    /** Speed multiplier for bullets fired with this power-up (1.0 = normal). */
    public double getBulletSpeedMultiplier() {
        return 1.0;
    }

    /** @return true if a tank holding this power-up survives being hit. */
    public boolean providesShield() {
        return false;
    }
}

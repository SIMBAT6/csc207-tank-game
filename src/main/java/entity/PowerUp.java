package entity;

import java.util.List;

/**
 * A pickup that gives a tank a special weapon or ability for a limited time (UC4).
 *
 * <p>A power-up lies on the map until a tank drives over it. It then changes the tank only
 * through the tank's power-up hooks ({@code setWeapon}, {@code setShielded}, {@code setRadius})
 * in {@link #applyTo(Tank)}, and undoes that in {@link #removeFrom(Tank, Maze)} when it expires
 * or is replaced. New power-ups are added by subclassing (Open/Closed Principle).</p>
 */
public abstract class PowerUp {
    /** Ammo value meaning "not limited by shots". */
    public static final int UNLIMITED_AMMO = -1;

    private final double x;
    private final double y;
    private int duration;
    private int ammo;

    /**
     * @param x        x position (pixels) of the power-up's centre on the map
     * @param y        y position (pixels) of the power-up's centre on the map
     * @param duration how many ticks the effect lasts once collected
     * @param ammo     shots before it runs out, or {@link #UNLIMITED_AMMO}
     */
    protected PowerUp(double x, double y, int duration, int ammo) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.ammo = ammo;
    }

    /** @return which kind of power-up this is, for the HUD and the map icon. */
    public abstract PowerUpType getType();

    /** Gives this power-up's effect to the tank that collected it. */
    public abstract void applyTo(Tank tank);

    /**
     * Takes the effect away again, so the tank is back to normal.
     *
     * @param tank the tank holding this power-up
     * @param maze the current maze (needed to check there is room for the tank to grow back)
     * @return true if the tank is fully back to normal; false if it must be tried again next tick
     */
    public abstract boolean removeFrom(Tank tank, Maze maze);

    /**
     * Points {x, y} of an aim line to draw while a tank holds this power-up.
     * Most power-ups have none; the laser shows where its beam would bounce.
     */
    public List<double[]> getAimPath(Tank holder, Maze maze) {
        return List.of();
    }

    /** Counts the duration down by one tick. Call once per tick while a tank holds it. */
    public void tick() {
        if (duration > 0) {
            duration--;
        }
    }

    /** Uses up one shot. Called by the power-up's weapon each time the tank fires. */
    protected void consumeAmmo() {
        if (ammo > 0) {
            ammo--;
        }
    }

    /**
     * @param holder the tank holding this power-up
     * @return true when the duration has run out or the ammo is used up
     */
    public boolean isExpired(Tank holder) {
        return duration <= 0 || ammo == 0;
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

    public int getAmmo() {
        return ammo;
    }
}

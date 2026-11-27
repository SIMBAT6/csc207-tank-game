package entity;

import java.util.List;

/**
 * A tank controlled by one player. For collisions a tank is a circle of radius getRadius().
 * Angles are in radians; 0 faces right and angles grow clockwise (screen y points down).
 *
 * <p>Power-ups (UC4) change a tank only through setWeapon, setRadius / tryResetRadius
 * and setShielded.</p>
 */
public class Tank {
    public static final double DEFAULT_RADIUS = 14;
    public static final double SPEED = 3;
    public static final double ROTATION_SPEED = 0.08;
    public static final int FIRE_COOLDOWN = 15;
    public static final int MAX_BULLETS = 5;
    /** The barrel reaches this many radii from the centre; bullets leave from its tip. */
    public static final double BARREL_LENGTH_RATIO = 1.4;

    private final int id;
    private double x;
    private double y;
    private double angle;
    private double radius = DEFAULT_RADIUS;
    private boolean alive = true;
    private boolean shielded;
    private int cooldown;
    private Weapon weapon = new StandardWeapon();

    public Tank(int id, double x, double y, double angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /**
     * Turns the tank by one step.
     *
     * @param direction 1 to turn clockwise, -1 counter-clockwise, 0 to keep the current angle
     */
    public void rotate(int direction) {
        if (alive) {
            angle += direction * ROTATION_SPEED;
        }
    }

    /**
     * Drives one step forward or backward. If the full step would hit a wall, the tank
     * tries moving along only one axis so it slides along the wall instead of sticking.
     *
     * @param direction 1 for forward, -1 for backward, 0 to stay still
     */
    public void drive(int direction, Maze maze) {
        if (!alive || direction == 0) {
            return;
        }
        double dx = Math.cos(angle) * SPEED * direction;
        double dy = Math.sin(angle) * SPEED * direction;
        if (!maze.blocksCircle(x + dx, y + dy, radius)) {
            x += dx;
            y += dy;
        } else if (!maze.blocksCircle(x + dx, y, radius)) {
            x += dx;
        } else if (!maze.blocksCircle(x, y + dy, radius)) {
            y += dy;
        }
    }

    /** Counts the fire cooldown down by one tick. Call once per tick. */
    public void coolDown() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    /**
     * @param activeBullets how many of this tank's bullets are still on the field
     */
    public boolean canFire(int activeBullets) {
        return alive && cooldown == 0 && activeBullets < MAX_BULLETS;
    }

    /** Fires the current weapon and restarts the cooldown. Check canFire first. */
    public List<Bullet> fire() {
        cooldown = FIRE_COOLDOWN;
        return weapon.createBullets(this);
    }

    /**
     * Called when a bullet hits this tank. A shield absorbs the hit and is used up;
     * otherwise the tank is destroyed.
     *
     * @return true if the tank was destroyed
     */
    public boolean takeHit() {
        if (shielded) {
            shielded = false;
            return false;
        }
        destroy();
        return true;
    }

    public void destroy() {
        alive = false;
    }

    /**
     * Grows back to DEFAULT_RADIUS, unless that would overlap a wall at the current position.
     *
     * @return true if the tank is back to its default size
     */
    public boolean tryResetRadius(Maze maze) {
        if (maze.blocksCircle(x, y, DEFAULT_RADIUS)) {
            return false;
        }
        radius = DEFAULT_RADIUS;
        return true;
    }

    public double getRadius() {
        return radius;
    }

    /** How far the barrel tip is from the centre. It shrinks with the tank. */
    public double getBarrelLength() {
        return radius * BARREL_LENGTH_RATIO;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isShielded() {
        return shielded;
    }

    public void setShielded(boolean shielded) {
        this.shielded = shielded;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
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

    public boolean isAlive() {
        return alive;
    }
}

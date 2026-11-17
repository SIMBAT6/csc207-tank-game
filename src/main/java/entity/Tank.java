package entity;

import java.util.List;

/**
 * A tank controlled by one player. For collisions a tank is a circle of radius RADIUS.
 * Angles are in radians; 0 faces right and angles grow clockwise (screen y points down).
 */
public class Tank {
    public static final double RADIUS = 14;
    public static final double SPEED = 2.0;
    public static final double ROTATION_SPEED = 0.06;
    public static final int FIRE_COOLDOWN = 15;
    public static final int MAX_BULLETS = 5;

    private final int id;
    private double x;
    private double y;
    private double angle;
    private boolean alive = true;
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
        if (!maze.blocksCircle(x + dx, y + dy, RADIUS)) {
            x += dx;
            y += dy;
        } else if (!maze.blocksCircle(x + dx, y, RADIUS)) {
            x += dx;
        } else if (!maze.blocksCircle(x, y + dy, RADIUS)) {
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

    public void destroy() {
        alive = false;
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

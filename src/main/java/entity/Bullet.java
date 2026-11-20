package entity;

import java.util.List;

/**
 * A bullet that travels in a straight line, bounces off walls and disappears after its lifetime.
 * For collisions a bullet is a circle of radius getRadius().
 *
 * <p>Each tick the game moves a bullet getStepsPerTick() times, checking for hits after every
 * step, then calls age() once. Power-ups (UC4) make special bullets by subclassing and
 * overriding the protected/overridable hooks: getRadius, getStepsPerTick, hitsWall,
 * onHitTank and getPath.</p>
 *
 * <p>Bullets start at the centre of the tank that fired them, so they cannot hit that tank
 * until they have travelled ARMING_DISTANCE. After that they can bounce back and hit it.</p>
 *
 * <p>Each step must be shorter than 2 * getRadius(), otherwise a bullet could skip over a
 * thin wall. Go faster with more steps per tick, not a bigger velocity.</p>
 */
public class Bullet {
    public static final double DEFAULT_RADIUS = 3;
    public static final double SPEED = 4.0;
    public static final int LIFETIME = 600;
    public static final double ARMING_DISTANCE = 40;

    private final int ownerId;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private int lifetime;
    private double distanceTravelled;

    public Bullet(int ownerId, double x, double y, double velocityX, double velocityY) {
        this(ownerId, x, y, velocityX, velocityY, LIFETIME);
    }

    /**
     * @param lifetime how many ticks the bullet lasts
     */
    protected Bullet(int ownerId, double x, double y, double velocityX, double velocityY, int lifetime) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.lifetime = lifetime;
    }

    /**
     * Moves one step. Each axis moves separately: if moving along an axis hits a wall,
     * the bullet stays put on that axis and reverses its velocity there (a bounce).
     * A bullet that ends up outside the maze expires.
     */
    public void update(Maze maze) {
        x += velocityX;
        if (hitsWall(maze)) {
            x -= velocityX;
            velocityX = -velocityX;
        }
        y += velocityY;
        if (hitsWall(maze)) {
            y -= velocityY;
            velocityY = -velocityY;
        }
        distanceTravelled += Math.hypot(velocityX, velocityY);
        if (!maze.contains(x, y)) {
            expire();
        }
    }

    /** Counts the lifetime down by one tick. Call once per tick, after all steps. */
    public void age() {
        lifetime--;
    }

    public boolean hits(Tank tank) {
        if (!tank.isAlive() || isExpired()) {
            return false;
        }
        boolean stillLeavingOwner = tank.getId() == ownerId && distanceTravelled < ARMING_DISTANCE;
        if (stillLeavingOwner) {
            return false;
        }
        double dx = tank.getX() - x;
        double dy = tank.getY() - y;
        double reach = tank.getRadius() + getRadius();
        return dx * dx + dy * dy < reach * reach;
    }

    /** Whether the bullet at its current position counts as hitting a wall. Override to pierce walls. */
    protected boolean hitsWall(Maze maze) {
        return maze.blocksCircle(x, y, getRadius());
    }

    /** What happens after this bullet hits a tank. By default it disappears. */
    public void onHitTank() {
        expire();
    }

    public double getRadius() {
        return DEFAULT_RADIUS;
    }

    /** How many steps the bullet moves each tick. Override for faster projectiles. */
    public int getStepsPerTick() {
        return 1;
    }

    /**
     * The points to draw: just the current position for a bullet; a beam overrides this
     * to return its whole path.
     */
    public List<double[]> getPath() {
        return List.of(new double[] {x, y});
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    /** Removes the bullet at the end of this tick, e.g. after it hits a tank. */
    public void expire() {
        lifetime = 0;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }
}

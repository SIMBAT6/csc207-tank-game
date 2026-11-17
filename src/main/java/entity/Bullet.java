package entity;

/**
 * A bullet that travels in a straight line, bounces off walls and disappears after LIFETIME ticks.
 * For collisions a bullet is a circle of radius RADIUS.
 *
 * <p>Bullets start at the centre of the tank that fired them, so they cannot hit that tank
 * until ARMING_TICKS have passed (by then they have left it). After that they can bounce
 * back and hit it.</p>
 *
 * <p>SPEED must stay below 2 * RADIUS, otherwise a bullet could skip over a thin wall.</p>
 */
public class Bullet {
    public static final double RADIUS = 3;
    public static final double SPEED = 4.0;
    public static final int LIFETIME = 600;
    public static final int ARMING_TICKS = 10;

    private final int ownerId;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private int lifetime = LIFETIME;

    public Bullet(int ownerId, double x, double y, double velocityX, double velocityY) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Moves one tick. Each axis moves separately: if moving along an axis would hit a wall,
     * the bullet stays put on that axis and reverses its velocity there (a bounce).
     */
    public void update(Maze maze) {
        x += velocityX;
        if (maze.blocksCircle(x, y, RADIUS)) {
            x -= velocityX;
            velocityX = -velocityX;
        }
        y += velocityY;
        if (maze.blocksCircle(x, y, RADIUS)) {
            y -= velocityY;
            velocityY = -velocityY;
        }
        lifetime--;
    }

    public boolean hits(Tank tank) {
        if (!tank.isAlive() || isExpired()) {
            return false;
        }
        boolean stillInsideOwner = tank.getId() == ownerId && LIFETIME - lifetime < ARMING_TICKS;
        if (stillInsideOwner) {
            return false;
        }
        double dx = tank.getX() - x;
        double dy = tank.getY() - y;
        double reach = Tank.RADIUS + RADIUS;
        return dx * dx + dy * dy < reach * reach;
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
}

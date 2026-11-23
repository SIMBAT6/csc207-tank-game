package entity;

/**
 * A heavy cannon ball that flies straight through walls instead of bouncing (UC4).
 * It disappears when it leaves the maze, hits a tank or its lifetime runs out.
 */
public class CannonBullet extends Bullet {
    public static final double RADIUS = 6;

    public CannonBullet(int ownerId, double x, double y, double velocityX, double velocityY) {
        super(ownerId, x, y, velocityX, velocityY);
    }

    @Override
    protected boolean hitsWall(Maze maze) {
        return false;
    }

    @Override
    public double getRadius() {
        return RADIUS;
    }
}

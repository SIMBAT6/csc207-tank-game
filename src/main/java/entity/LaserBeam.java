package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A laser beam (UC4). On the tick it is fired it travels its whole length at once
 * ({@link #MAX_STEPS} normal-size steps, so it never skips through a wall), bouncing off walls
 * and hitting any tank on the way. It then stops and stays visible for a short time so the
 * player can see its path. {@link #getPath()} returns the start, every bounce point and the end.
 */
public class LaserBeam extends Bullet {
    /** Steps in the first tick: 200 steps x 4 px = 800 px of beam. */
    public static final int MAX_STEPS = 200;
    /** Ticks the beam stays on screen (a third of a second). */
    public static final int VISIBLE_TICKS = 20;

    private final List<double[]> bouncePoints = new ArrayList<>();
    private boolean finished;

    public LaserBeam(int ownerId, double x, double y, double velocityX, double velocityY) {
        super(ownerId, x, y, velocityX, velocityY, VISIBLE_TICKS);
        bouncePoints.add(new double[] {x, y});
    }

    /** Moves one step and records the point if the beam bounced off a wall. */
    @Override
    public void update(Maze maze) {
        final double oldVelocityX = getVelocityX();
        final double oldVelocityY = getVelocityY();
        super.update(maze);
        if (getVelocityX() != oldVelocityX || getVelocityY() != oldVelocityY) {
            bouncePoints.add(new double[] {getX(), getY()});
        }
    }

    /** All of the beam's length in the first tick, then it stays still. */
    @Override
    public int getStepsPerTick() {
        return finished ? 0 : MAX_STEPS;
    }

    /** The first tick is over: stop moving, just stay visible until the lifetime runs out. */
    @Override
    public void age() {
        finished = true;
        super.age();
    }

    /** A beam stops where it hits a tank but stays visible, instead of vanishing at once. */
    @Override
    public void onHitTank() {
        finished = true;
    }

    /** A beam that stopped on a tank must not hit again. */
    @Override
    public boolean hits(Tank tank) {
        return !finished && super.hits(tank);
    }

    @Override
    public List<double[]> getPath() {
        final List<double[]> path = new ArrayList<>(bouncePoints);
        path.add(new double[] {getX(), getY()});
        return path;
    }
}

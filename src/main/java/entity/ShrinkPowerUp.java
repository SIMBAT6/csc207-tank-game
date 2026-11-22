package entity;

/**
 * Makes the tank smaller (a harder target that fits through tighter gaps) for 5 seconds.
 * When it runs out, the tank only grows back once there is room, so it never ends up
 * stuck inside a wall.
 */
public class ShrinkPowerUp extends PowerUp {
    public static final int DURATION = 300;
    public static final double SHRUNK_RADIUS = 8;

    public ShrinkPowerUp(double x, double y) {
        super(x, y, DURATION, UNLIMITED_AMMO);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.SHRINK;
    }

    @Override
    public void applyTo(Tank tank) {
        tank.setRadius(SHRUNK_RADIUS);
    }

    /** @return false while a wall is too close for the tank to grow back; try again next tick. */
    @Override
    public boolean removeFrom(Tank tank, Maze maze) {
        return tank.tryResetRadius(maze);
    }
}

package entity;

import java.util.List;

/**
 * Fires a laser beam that crosses the whole maze in one tick, bouncing off walls.
 * Two shots, or 10 seconds. While a tank holds it, {@link #getAimPath} gives the aim line.
 */
public class LaserPowerUp extends PowerUp implements Weapon {
    public static final int DURATION = 600;
    public static final int AMMO = 2;

    public LaserPowerUp(double x, double y) {
        super(x, y, DURATION, AMMO);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.LASER;
    }

    @Override
    public void applyTo(Tank tank) {
        tank.setWeapon(this);
    }

    @Override
    public boolean removeFrom(Tank tank, Maze maze) {
        tank.setWeapon(new StandardWeapon());
        return true;
    }

    @Override
    public List<Bullet> createBullets(Tank tank) {
        consumeAmmo();
        return List.of(beamFrom(tank));
    }

    /**
     * The aim line: simulates a probe beam (no hit checks) to show where a shot would go.
     *
     * @return the start, bounce points and end of the beam the tank would fire now
     */
    @Override
    public List<double[]> getAimPath(Tank tank, Maze maze) {
        final LaserBeam probe = beamFrom(tank);
        for (int i = 0; i < LaserBeam.MAX_STEPS && !probe.isExpired(); i++) {
            probe.update(maze);
        }
        return probe.getPath();
    }

    private static LaserBeam beamFrom(Tank tank) {
        return new LaserBeam(tank.getId(), tank.getX(), tank.getY(),
                Math.cos(tank.getAngle()) * Bullet.SPEED, Math.sin(tank.getAngle()) * Bullet.SPEED);
    }
}

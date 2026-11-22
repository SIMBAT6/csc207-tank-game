package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fires a spread of five bullets instead of one. Lasts 3 shots or 10 seconds.
 */
public class ShotgunPowerUp extends PowerUp implements Weapon {
    public static final int DURATION = 600;
    public static final int AMMO = 3;
    public static final int PELLETS = 5;
    /** Angle between two neighbouring pellets, in radians (15 degrees). */
    public static final double SPREAD = Math.toRadians(15);

    public ShotgunPowerUp(double x, double y) {
        super(x, y, DURATION, AMMO);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.SHOTGUN;
    }

    @Override
    public void applyTo(Tank tank) {
        tank.setWeapon(this);
    }

    @Override
    public void removeFrom(Tank tank) {
        tank.setWeapon(new StandardWeapon());
    }

    /** Five bullets at -30, -15, 0, 15 and 30 degrees around the tank's facing direction. */
    @Override
    public List<Bullet> createBullets(Tank tank) {
        consumeAmmo();
        final List<Bullet> bullets = new ArrayList<>();
        for (int i = 0; i < PELLETS; i++) {
            final double angle = tank.getAngle() + (i - PELLETS / 2) * SPREAD;
            bullets.add(new Bullet(tank.getId(), tank.getX(), tank.getY(),
                    Math.cos(angle) * Bullet.SPEED, Math.sin(angle) * Bullet.SPEED));
        }
        return bullets;
    }
}

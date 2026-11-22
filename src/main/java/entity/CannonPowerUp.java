package entity;

import java.util.List;

/**
 * Fires one cannon ball that goes through walls. One shot, or 10 seconds.
 */
public class CannonPowerUp extends PowerUp implements Weapon {
    public static final int DURATION = 600;
    public static final int AMMO = 1;

    public CannonPowerUp(double x, double y) {
        super(x, y, DURATION, AMMO);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.CANNON;
    }

    @Override
    public void applyTo(Tank tank) {
        tank.setWeapon(this);
    }

    @Override
    public void removeFrom(Tank tank) {
        tank.setWeapon(new StandardWeapon());
    }

    @Override
    public List<Bullet> createBullets(Tank tank) {
        consumeAmmo();
        return List.of(new CannonBullet(tank.getId(), tank.getX(), tank.getY(),
                Math.cos(tank.getAngle()) * Bullet.SPEED, Math.sin(tank.getAngle()) * Bullet.SPEED));
    }
}

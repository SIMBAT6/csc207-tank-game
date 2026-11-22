package entity;

/**
 * Protects the tank from one hit. Lasts until it absorbs a hit or 5 seconds pass.
 * Uses the tank's own shield: Tank.takeHit() uses the shield up.
 */
public class ShieldPowerUp extends PowerUp {
    public static final int DURATION = 300;

    public ShieldPowerUp(double x, double y) {
        super(x, y, DURATION, UNLIMITED_AMMO);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.SHIELD;
    }

    @Override
    public void applyTo(Tank tank) {
        tank.setShielded(true);
    }

    @Override
    public void removeFrom(Tank tank) {
        tank.setShielded(false);
    }

    /** Also expired once the shield has absorbed a hit. */
    @Override
    public boolean isExpired(Tank holder) {
        return super.isExpired(holder) || !holder.isShielded();
    }
}

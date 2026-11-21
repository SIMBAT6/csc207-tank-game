package entity;

/** Fires a single very fast shot. One use only. */
public class LaserPowerUp extends PowerUp {

    public static final int DURATION_TICKS = 600;
    public static final int AMMO = 1;
    public static final double SPEED_MULTIPLIER = 4.0;

    public LaserPowerUp(double x, double y) {
        super(x, y, DURATION_TICKS, PowerUpType.LASER, AMMO);
    }

    @Override
    public double[] getShotAngleOffsets() {
        return new double[] {0.0};
    }

    @Override
    public double getBulletSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}

package entity;

/** Fires a spread of three bullets. Limited to a few shots. */
public class ShotgunPowerUp extends PowerUp {

    public static final int DURATION_TICKS = 600;
    public static final int AMMO = 3;
    private static final double[] SPREAD = {-15.0, 0.0, 15.0};

    public ShotgunPowerUp(double x, double y) {
        super(x, y, DURATION_TICKS, PowerUpType.SHOTGUN, AMMO);
    }

    @Override
    public double[] getShotAngleOffsets() {
        return SPREAD.clone();
    }
}

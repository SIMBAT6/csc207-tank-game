package entity;

/** Protects the tank from bullets for a limited time. Does not change the weapon. */
public class ShieldPowerUp extends PowerUp {

    public static final int DURATION_TICKS = 300;

    public ShieldPowerUp(double x, double y) {
        super(x, y, DURATION_TICKS, PowerUpType.SHIELD, UNLIMITED_AMMO);
    }

    @Override
    public double[] getShotAngleOffsets() {
        return new double[] {0.0};
    }

    @Override
    public boolean providesShield() {
        return true;
    }
}

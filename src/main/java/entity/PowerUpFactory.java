package entity;

import java.util.Random;

/**
 * Factory pattern: the only place that knows the concrete power-up classes.
 */
public class PowerUpFactory {

    private final Random random;

    public PowerUpFactory(Random random) {
        this.random = random;
    }

    public PowerUp create(PowerUpType type, double x, double y) {
        switch (type) {
            case SHOTGUN:
                return new ShotgunPowerUp(x, y);
            case LASER:
                return new LaserPowerUp(x, y);
            case SHIELD:
                return new ShieldPowerUp(x, y);
            default:
                throw new IllegalArgumentException("Unknown power-up type: " + type);
        }
    }

    public PowerUp createRandom(double x, double y) {
        PowerUpType[] types = PowerUpType.values();
        return create(types[random.nextInt(types.length)], x, y);
    }
}

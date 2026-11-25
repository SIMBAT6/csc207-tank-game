package use_case.collect_power_up;

import entity.Match;
import entity.PowerUp;
import entity.Tank;

/**
 * What Collect Power-Up needs from the game state. Implemented by the in-memory
 * data access object (Dependency Inversion: the use case owns this interface).
 */
public interface CollectPowerUpDataAccessInterface {

    /** @return the match being played, or null before a match has started. */
    Match getCurrentMatch();

    /** @return the power-up this tank holds, or null if it has none. */
    PowerUp getActivePowerUp(Tank tank);

    /**
     * @param tank    the tank
     * @param powerUp the power-up it now holds, or null to clear it
     */
    void setActivePowerUp(Tank tank, PowerUp powerUp);
}

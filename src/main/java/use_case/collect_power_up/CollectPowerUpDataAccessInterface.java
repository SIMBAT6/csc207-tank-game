package use_case.collect_power_up;

import java.util.List;

import entity.PowerUp;
import entity.Tank;

/**
 * What the Collect Power-Up interactor needs from the current game state.
 * Implemented by the in-memory game-state DAO (Dependency Inversion: the use case
 * owns this interface, the data-access layer implements it).
 */
public interface CollectPowerUpDataAccessInterface {

    /** @return the tanks in the current round (index 0 = Player 1, index 1 = Player 2). */
    List<Tank> getTanks();

    /** @return the power-ups currently lying on the map (not yet collected). */
    List<PowerUp> getPowerUpsOnMap();

    void addPowerUp(PowerUp powerUp);

    void removePowerUp(PowerUp powerUp);

    /** @return centre points {x, y} (pixels) of every maze cell that is not a wall. */
    List<double[]> getEmptyCellCentres();
}

package data_access;

import java.util.IdentityHashMap;
import java.util.Map;

import entity.Match;
import entity.PowerUp;
import entity.Tank;
import use_case.collect_power_up.CollectPowerUpDataAccessInterface;

/**
 * Keeps the current match and which power-up each tank holds, in memory (UC4).
 * Start Match (UC2) calls {@link #setCurrentMatch(Match)} when a match starts.
 */
public class InMemoryPowerUpDataAccessObject implements CollectPowerUpDataAccessInterface {
    private Match currentMatch;
    private final Map<Tank, PowerUp> activePowerUps = new IdentityHashMap<>();

    @Override
    public Match getCurrentMatch() {
        return currentMatch;
    }

    public void setCurrentMatch(Match match) {
        this.currentMatch = match;
        activePowerUps.clear();
    }

    @Override
    public PowerUp getActivePowerUp(Tank tank) {
        return activePowerUps.get(tank);
    }

    @Override
    public void setActivePowerUp(Tank tank, PowerUp powerUp) {
        if (powerUp == null) {
            activePowerUps.remove(tank);
        }
        else {
            activePowerUps.put(tank, powerUp);
        }
    }
}

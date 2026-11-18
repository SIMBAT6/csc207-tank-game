package use_case.view_leaderboard;

import entity.Player;
import use_case.DataAccessException;

import java.util.List;

/**
 * Loads every account's saved record (through the account API).
 */
public interface ViewLeaderboardDataAccessInterface {

    /**
     * @return one Player per account that has a saved record, in no particular order
     * @throws DataAccessException if the records could not be loaded
     */
    List<Player> getAllPlayers() throws DataAccessException;
}

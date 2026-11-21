package use_case.end_round;

import use_case.DataAccessException;

/**
 * Saves a finished round's result to a player's record (through the account API).
 */
public interface EndRoundStatsDataAccessInterface {

    /**
     * Adds one win or one loss to a player's saved record.
     *
     * @param username the logged-in player's username
     * @param won      true for a win, false for a loss
     * @throws DataAccessException if the record could not be saved
     */
    void recordResult(String username, boolean won) throws DataAccessException;
}

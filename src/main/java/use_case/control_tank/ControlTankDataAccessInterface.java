package use_case.control_tank;

import entity.Match;

/**
 * Gives Control Tank the match being played. Implemented by the in-memory match data access object (UC2).
 */
public interface ControlTankDataAccessInterface {

    /** @return the match being played, or null before a match has started. */
    Match getCurrentMatch();
}

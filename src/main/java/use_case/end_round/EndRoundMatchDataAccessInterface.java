package use_case.end_round;

import entity.Match;

/**
 * Gives End Round the match being played. Implemented by the in-memory match data access object.
 */
public interface EndRoundMatchDataAccessInterface {
    Match getCurrentMatch();
}

package use_case.end_round;

/**
 * Shows the result of a round.
 */
public interface EndRoundOutputBoundary {

    /** The round is over but the match goes on; a new round starts after a short delay. */
    void prepareRoundOverView(EndRoundOutputData outputData);

    /** Someone reached the wins needed; the match is over. */
    void prepareMatchOverView(EndRoundOutputData outputData);
}

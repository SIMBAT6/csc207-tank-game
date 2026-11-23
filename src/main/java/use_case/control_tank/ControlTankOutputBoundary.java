package use_case.control_tank;

/**
 * Receives what to draw after each tick.
 */
public interface ControlTankOutputBoundary {

    /** The round is still going. */
    void prepareTickView(ControlTankOutputData outputData);

    /** Fewer than two tanks are alive: the round is over and {@code outputData.getWinner()} says who won. */
    void prepareRoundOverView(ControlTankOutputData outputData);
}

package use_case.end_round;

/**
 * Called when a round has finished (a tank was destroyed).
 */
public interface EndRoundInputBoundary {
    void execute(EndRoundInputData inputData);
}

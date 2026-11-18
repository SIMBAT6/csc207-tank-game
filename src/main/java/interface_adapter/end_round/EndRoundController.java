package interface_adapter.end_round;

import use_case.end_round.EndRoundInputBoundary;
import use_case.end_round.EndRoundInputData;

/**
 * Called by the game screen when Control Tank (UC3) reports that the round is over.
 */
public class EndRoundController {
    private final EndRoundInputBoundary interactor;

    public EndRoundController(EndRoundInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * @param winner {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.DRAW}
     */
    public void execute(int winner) {
        interactor.execute(new EndRoundInputData(winner));
    }
}

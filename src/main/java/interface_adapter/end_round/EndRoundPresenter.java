package interface_adapter.end_round;

import use_case.end_round.EndRoundOutputBoundary;
import use_case.end_round.EndRoundOutputData;

/**
 * Turns a round result into the score bar and banner text.
 */
public class EndRoundPresenter implements EndRoundOutputBoundary {
    static final String NOT_SAVED_WARNING = "Results were not saved: could not reach the account server.";

    private final EndRoundViewModel viewModel;

    public EndRoundPresenter(EndRoundViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareRoundOverView(EndRoundOutputData outputData) {
        final String banner;
        if (outputData.getRoundWinnerName() == null) {
            banner = "Draw! No point this round";
        }
        else {
            banner = outputData.getRoundWinnerName() + " wins the round";
        }
        show(outputData, banner, false);
    }

    @Override
    public void prepareMatchOverView(EndRoundOutputData outputData) {
        show(outputData, outputData.getMatchWinnerName() + " wins the match!", true);
    }

    private void show(EndRoundOutputData outputData, String banner, boolean matchOver) {
        final EndRoundState state = viewModel.getState();
        state.setScore1(outputData.getScore1());
        state.setScore2(outputData.getScore2());
        state.setBanner(banner);
        state.setWarning(outputData.isResultsSaved() ? null : NOT_SAVED_WARNING);
        state.setMatchOver(matchOver);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}

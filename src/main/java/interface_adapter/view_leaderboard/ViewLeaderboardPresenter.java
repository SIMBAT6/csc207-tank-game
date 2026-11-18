package interface_adapter.view_leaderboard;

import interface_adapter.ViewManagerModel;
import use_case.view_leaderboard.ViewLeaderboardOutputBoundary;
import use_case.view_leaderboard.ViewLeaderboardOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Fills the leaderboard screen and switches to it.
 */
public class ViewLeaderboardPresenter implements ViewLeaderboardOutputBoundary {
    private final LeaderboardViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewLeaderboardPresenter(LeaderboardViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ViewLeaderboardOutputData outputData) {
        final List<String[]> rows = new ArrayList<>();
        for (ViewLeaderboardOutputData.Row row : outputData.getRows()) {
            rows.add(new String[] {
                String.valueOf(row.getRank()),
                row.getUsername(),
                String.valueOf(row.getWins()),
                String.valueOf(row.getLosses()),
            });
        }
        final LeaderboardState state = viewModel.getState();
        state.setRows(rows);
        state.setErrorMessage(null);
        show(state);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final LeaderboardState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        show(state);
    }

    private void show(LeaderboardState state) {
        viewModel.setState(state);
        viewModel.firePropertyChanged();
        viewManagerModel.setState(viewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}

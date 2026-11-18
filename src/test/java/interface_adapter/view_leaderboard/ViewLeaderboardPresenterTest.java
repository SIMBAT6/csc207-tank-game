package interface_adapter.view_leaderboard;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.Test;
import use_case.view_leaderboard.ViewLeaderboardOutputData;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ViewLeaderboardPresenterTest {

    @Test
    void successFillsTableAndSwitchesScreen() {
        final LeaderboardViewModel viewModel = new LeaderboardViewModel();
        final ViewManagerModel viewManagerModel = new ViewManagerModel();

        new ViewLeaderboardPresenter(viewModel, viewManagerModel).prepareSuccessView(
                new ViewLeaderboardOutputData(Arrays.asList(
                        new ViewLeaderboardOutputData.Row(1, "alice", 6, 2),
                        new ViewLeaderboardOutputData.Row(2, "bob", 1, 4))));

        assertEquals(2, viewModel.getState().getRows().size());
        assertArrayEquals(new String[] {"1", "alice", "6", "2"}, viewModel.getState().getRows().get(0));
        assertNull(viewModel.getState().getErrorMessage());
        assertEquals(LeaderboardViewModel.VIEW_NAME, viewManagerModel.getState());
    }

    @Test
    void failShowsErrorOnLeaderboardScreen() {
        final LeaderboardViewModel viewModel = new LeaderboardViewModel();
        final ViewManagerModel viewManagerModel = new ViewManagerModel();

        new ViewLeaderboardPresenter(viewModel, viewManagerModel).prepareFailView("server down");

        assertEquals("server down", viewModel.getState().getErrorMessage());
        assertEquals(LeaderboardViewModel.VIEW_NAME, viewManagerModel.getState());
    }
}

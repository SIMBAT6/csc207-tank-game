package interface_adapter.view_leaderboard;

import org.junit.jupiter.api.Test;
import use_case.view_leaderboard.ViewLeaderboardOutputData;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ViewLeaderboardPresenterTest {

    @Test
    void successFillsTable() {
        final LeaderboardViewModel viewModel = new LeaderboardViewModel();
        final int[] fired = {0};
        viewModel.addPropertyChangeListener(evt -> fired[0]++);

        new ViewLeaderboardPresenter(viewModel).prepareSuccessView(
                new ViewLeaderboardOutputData(Arrays.asList(
                        new ViewLeaderboardOutputData.Row(1, "alice", 6, 2),
                        new ViewLeaderboardOutputData.Row(2, "bob", 1, 4))));

        assertEquals(2, viewModel.getState().getRows().size());
        assertArrayEquals(new String[] {"1", "alice", "6", "2"}, viewModel.getState().getRows().get(0));
        assertNull(viewModel.getState().getErrorMessage());
        assertEquals(1, fired[0]);
    }

    @Test
    void failShowsError() {
        final LeaderboardViewModel viewModel = new LeaderboardViewModel();

        new ViewLeaderboardPresenter(viewModel).prepareFailView("server down");

        assertEquals("server down", viewModel.getState().getErrorMessage());
    }
}

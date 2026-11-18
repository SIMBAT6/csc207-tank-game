package use_case.view_leaderboard;

import entity.Player;
import org.junit.jupiter.api.Test;
import use_case.DataAccessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewLeaderboardInteractorTest {

    /** Remembers which view the interactor asked for. */
    private static class RecordingPresenter implements ViewLeaderboardOutputBoundary {
        private ViewLeaderboardOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(ViewLeaderboardOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }
    }

    @Test
    void playersAreRankedByWins() {
        final RecordingPresenter presenter = new RecordingPresenter();
        final List<Player> players = Arrays.asList(
                new Player("bob", false, 1, 4),
                new Player("alice", false, 6, 2));

        new ViewLeaderboardInteractor(() -> players, presenter).execute();

        final List<ViewLeaderboardOutputData.Row> rows = presenter.success.getRows();
        assertEquals(2, rows.size());
        assertEquals(1, rows.get(0).getRank());
        assertEquals("alice", rows.get(0).getUsername());
        assertEquals(6, rows.get(0).getWins());
        assertEquals(2, rows.get(0).getLosses());
        assertEquals(2, rows.get(1).getRank());
        assertEquals("bob", rows.get(1).getUsername());
        assertNull(presenter.error);
    }

    @Test
    void noRecordsYetGivesEmptyLeaderboard() {
        final RecordingPresenter presenter = new RecordingPresenter();

        new ViewLeaderboardInteractor(ArrayList::new, presenter).execute();

        assertTrue(presenter.success.getRows().isEmpty());
    }

    @Test
    void serverDownShowsError() {
        final RecordingPresenter presenter = new RecordingPresenter();
        final ViewLeaderboardDataAccessInterface offline = () -> {
            throw new DataAccessException("could not reach the account server");
        };

        new ViewLeaderboardInteractor(offline, presenter).execute();

        assertNull(presenter.success);
        assertEquals("Could not load the leaderboard: could not reach the account server", presenter.error);
    }
}

package use_case.end_round;

import entity.Match;
import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.DataAccessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndRoundInteractorTest {

    /** Saves results in a list, or fails every call when {@code offline} is true. */
    private static class FakeStats implements EndRoundStatsDataAccessInterface {
        private final List<String> saved = new ArrayList<>();
        private boolean offline;

        @Override
        public void recordResult(String username, boolean won) throws DataAccessException {
            if (offline) {
                throw new DataAccessException("offline");
            }
            saved.add(username + (won ? " win" : " loss"));
        }
    }

    /** Remembers which view the interactor asked for. */
    private static class RecordingPresenter implements EndRoundOutputBoundary {
        private EndRoundOutputData roundOver;
        private EndRoundOutputData matchOver;

        @Override
        public void prepareRoundOverView(EndRoundOutputData outputData) {
            roundOver = outputData;
        }

        @Override
        public void prepareMatchOverView(EndRoundOutputData outputData) {
            matchOver = outputData;
        }
    }

    private Player alice;
    private Player bob;
    private Match match;
    private FakeStats stats;
    private RecordingPresenter presenter;
    private EndRoundInteractor interactor;

    private void newMatch(Player player1, Player player2, int winsToWin) {
        alice = player1;
        bob = player2;
        match = new Match(player1, player2, null, null, null);
        match.setWinsToWin(winsToWin);
        stats = new FakeStats();
        presenter = new RecordingPresenter();
        interactor = new EndRoundInteractor(() -> match, stats, presenter);
    }

    @BeforeEach
    void setUp() {
        newMatch(new Player("alice", false, 0, 0), new Player("bob", false, 0, 0), 2);
    }

    @Test
    void winnerScoresAndBothResultsAreSaved() {
        interactor.execute(new EndRoundInputData(Match.PLAYER_1));

        final EndRoundOutputData data = presenter.roundOver;
        assertEquals(Match.PLAYER_1, data.getRoundWinner());
        assertEquals("alice", data.getRoundWinnerName());
        assertEquals(1, data.getScore1());
        assertEquals(0, data.getScore2());
        assertEquals(Match.NO_WINNER, data.getMatchWinner());
        assertNull(data.getMatchWinnerName());
        assertTrue(data.isResultsSaved());
        assertNull(presenter.matchOver);

        assertEquals(Arrays.asList("alice win", "bob loss"), stats.saved);
        assertEquals(1, alice.getWins());
        assertEquals(1, bob.getLosses());
    }

    @Test
    void player2CanWinTheRound() {
        interactor.execute(new EndRoundInputData(Match.PLAYER_2));

        assertEquals("bob", presenter.roundOver.getRoundWinnerName());
        assertEquals(0, presenter.roundOver.getScore1());
        assertEquals(1, presenter.roundOver.getScore2());
        assertEquals(Arrays.asList("bob win", "alice loss"), stats.saved);
    }

    @Test
    void drawGivesNoPointAndSavesNothing() {
        interactor.execute(new EndRoundInputData(Match.DRAW));

        final EndRoundOutputData data = presenter.roundOver;
        assertEquals(Match.DRAW, data.getRoundWinner());
        assertNull(data.getRoundWinnerName());
        assertEquals(0, data.getScore1());
        assertEquals(0, data.getScore2());
        assertTrue(data.isResultsSaved());
        assertTrue(stats.saved.isEmpty());
    }

    @Test
    void guestResultIsNotSaved() {
        newMatch(new Player("alice", false, 0, 0), new Player("Guest", true, 0, 0), 2);

        interactor.execute(new EndRoundInputData(Match.PLAYER_2));

        assertEquals(Arrays.asList("alice loss"), stats.saved);
        assertEquals(0, bob.getWins());
        assertTrue(presenter.roundOver.isResultsSaved());
    }

    @Test
    void serverDownStillUpdatesScoreButWarns() {
        stats.offline = true;

        interactor.execute(new EndRoundInputData(Match.PLAYER_1));

        assertEquals(1, presenter.roundOver.getScore1());
        assertFalse(presenter.roundOver.isResultsSaved());
    }

    @Test
    void reachingWinsToWinEndsTheMatch() {
        interactor.execute(new EndRoundInputData(Match.PLAYER_2));
        interactor.execute(new EndRoundInputData(Match.PLAYER_2));

        final EndRoundOutputData data = presenter.matchOver;
        assertEquals(Match.PLAYER_2, data.getMatchWinner());
        assertEquals("bob", data.getMatchWinnerName());
        assertEquals(2, data.getScore2());
    }
}

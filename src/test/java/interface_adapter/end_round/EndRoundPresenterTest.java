package interface_adapter.end_round;

import entity.Match;
import org.junit.jupiter.api.Test;
import use_case.end_round.EndRoundOutputData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndRoundPresenterTest {

    @Test
    void roundWinShowsWinnerAndScore() {
        final EndRoundViewModel viewModel = new EndRoundViewModel();
        final int[] fired = {0};
        viewModel.addPropertyChangeListener(evt -> fired[0]++);

        new EndRoundPresenter(viewModel).prepareRoundOverView(new EndRoundOutputData(
                Match.PLAYER_1, "alice", 1, 0, Match.NO_WINNER, null, true));

        final EndRoundState state = viewModel.getState();
        assertEquals("alice wins the round", state.getBanner());
        assertEquals(1, state.getScore1());
        assertEquals(0, state.getScore2());
        assertNull(state.getWarning());
        assertFalse(state.isMatchOver());
        assertEquals(1, fired[0]);
    }

    @Test
    void drawAndUnsavedResults() {
        final EndRoundViewModel viewModel = new EndRoundViewModel();

        new EndRoundPresenter(viewModel).prepareRoundOverView(new EndRoundOutputData(
                Match.DRAW, null, 0, 0, Match.NO_WINNER, null, false));

        assertEquals("Draw! No point this round", viewModel.getState().getBanner());
        assertEquals(EndRoundPresenter.NOT_SAVED_WARNING, viewModel.getState().getWarning());
    }

    @Test
    void matchOverShowsMatchWinner() {
        final EndRoundViewModel viewModel = new EndRoundViewModel();

        new EndRoundPresenter(viewModel).prepareMatchOverView(new EndRoundOutputData(
                Match.PLAYER_2, "bob", 0, 2, Match.PLAYER_2, "bob", true));

        assertEquals("bob wins the match!", viewModel.getState().getBanner());
        assertTrue(viewModel.getState().isMatchOver());
    }
}

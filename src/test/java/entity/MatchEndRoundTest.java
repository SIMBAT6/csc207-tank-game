package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchEndRoundTest {
    private Player alice;
    private Player bob;
    private Match match;

    @BeforeEach
    void setUp() {
        alice = new Player("alice", false, 0, 0);
        bob = new Player("bob", false, 0, 0);
        match = new Match(alice, bob, null, null, null);
    }

    @Test
    void winnerGetsAPoint() {
        match.endRound(Match.PLAYER_1);
        match.endRound(Match.PLAYER_2);
        match.endRound(Match.PLAYER_2);

        assertEquals(1, match.getScore1());
        assertEquals(2, match.getScore2());
        assertTrue(match.isRoundOver());
    }

    @Test
    void drawGivesNoPoint() {
        match.endRound(Match.DRAW);

        assertEquals(0, match.getScore1());
        assertEquals(0, match.getScore2());
        assertTrue(match.isRoundOver());
    }

    @Test
    void unknownWinnerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> match.endRound(7));
        assertFalse(match.isRoundOver());
    }

    @Test
    void bestOfOneEndsAfterOneWin() {
        assertEquals(1, match.getWinsToWin());
        assertFalse(match.isMatchOver());
        assertEquals(Match.NO_WINNER, match.getMatchWinner());

        match.endRound(Match.PLAYER_2);

        assertTrue(match.isMatchOver());
        assertEquals(Match.PLAYER_2, match.getMatchWinner());
    }

    @Test
    void bestOfThreeNeedsTwoWins() {
        match.setWinsToWin(2);

        match.endRound(Match.PLAYER_1);
        match.endRound(Match.DRAW);
        match.endRound(Match.PLAYER_2);
        assertFalse(match.isMatchOver());

        match.endRound(Match.PLAYER_1);
        assertTrue(match.isMatchOver());
        assertEquals(Match.PLAYER_1, match.getMatchWinner());
    }

    @Test
    void winsToWinMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> match.setWinsToWin(0));
    }

    @Test
    void getPlayerBySide() {
        assertSame(alice, match.getPlayer(Match.PLAYER_1));
        assertSame(bob, match.getPlayer(Match.PLAYER_2));
        assertThrows(IllegalArgumentException.class, () -> match.getPlayer(Match.DRAW));
    }
}

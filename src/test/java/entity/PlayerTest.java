package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void recordWinAndLossUpdateCounts() {
        Player player = new Player("alice", false, 0, 0);

        player.recordWin();
        player.recordWin();
        player.recordLoss();

        assertEquals(2, player.getWins());
        assertEquals(1, player.getLosses());
    }
}

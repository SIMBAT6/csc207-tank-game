package entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderboardTest {

    private static List<String> names(List<Player> players) {
        final List<String> result = new ArrayList<>();
        for (Player player : players) {
            result.add(player.getUsername());
        }
        return result;
    }

    @Test
    void moreWinsRanksHigher() {
        final Leaderboard leaderboard = new Leaderboard(Arrays.asList(
                new Player("carol", false, 1, 0),
                new Player("alice", false, 5, 2),
                new Player("bob", false, 3, 9)));

        assertEquals(Arrays.asList("alice", "bob", "carol"), names(leaderboard.getRanking()));
    }

    @Test
    void equalWinsFewerLossesFirstThenName() {
        final Leaderboard leaderboard = new Leaderboard(Arrays.asList(
                new Player("dave", false, 2, 3),
                new Player("bob", false, 2, 1),
                new Player("alice", false, 2, 1)));

        assertEquals(Arrays.asList("alice", "bob", "dave"), names(leaderboard.getRanking()));
    }

    @Test
    void emptyLeaderboard() {
        assertTrue(new Leaderboard(new ArrayList<>()).getRanking().isEmpty());
    }

    @Test
    void rankingIsACopy() {
        final Leaderboard leaderboard = new Leaderboard(Arrays.asList(new Player("alice", false, 1, 0)));
        leaderboard.getRanking().clear();

        assertEquals(1, leaderboard.getRanking().size());
    }
}

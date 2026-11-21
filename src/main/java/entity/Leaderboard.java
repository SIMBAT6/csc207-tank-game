package entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Every account's win/loss record, ranked best first.
 * Ranking rule: more wins first; on equal wins, fewer losses first; then by username (A to Z).
 */
public class Leaderboard {
    private static final Comparator<Player> RANKING = Comparator
            .comparingInt(Player::getWins).reversed()
            .thenComparingInt(Player::getLosses)
            .thenComparing(Player::getUsername);

    private final List<Player> ranking;

    public Leaderboard(List<Player> players) {
        ranking = new ArrayList<>(players);
        ranking.sort(RANKING);
    }

    /** The players from best to worst. The returned list is a copy. */
    public List<Player> getRanking() {
        return new ArrayList<>(ranking);
    }
}

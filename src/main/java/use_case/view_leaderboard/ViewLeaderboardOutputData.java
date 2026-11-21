package use_case.view_leaderboard;

import java.util.ArrayList;
import java.util.List;

/**
 * The ranked leaderboard, as plain values.
 */
public class ViewLeaderboardOutputData {
    private final List<Row> rows;

    public ViewLeaderboardOutputData(List<Row> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /** Rows from rank 1 down. */
    public List<Row> getRows() {
        return new ArrayList<>(rows);
    }

    /**
     * One line of the leaderboard.
     */
    public static class Row {
        private final int rank;
        private final String username;
        private final int wins;
        private final int losses;

        public Row(int rank, String username, int wins, int losses) {
            this.rank = rank;
            this.username = username;
            this.wins = wins;
            this.losses = losses;
        }

        public int getRank() {
            return rank;
        }

        public String getUsername() {
            return username;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }
    }
}

package interface_adapter.view_leaderboard;

import java.util.ArrayList;
import java.util.List;

/**
 * What the leaderboard screen shows.
 */
public class LeaderboardState {
    private List<String[]> rows = new ArrayList<>();
    private String errorMessage;

    /** Table rows from rank 1 down; each row is {rank, username, wins, losses}. */
    public List<String[]> getRows() {
        return new ArrayList<>(rows);
    }

    public void setRows(List<String[]> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /** Why the leaderboard could not be loaded, or null. */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

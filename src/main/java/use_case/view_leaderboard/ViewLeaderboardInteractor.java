package use_case.view_leaderboard;

import entity.Leaderboard;
import entity.Player;
import use_case.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads every account's record, ranks them, and hands the ranking to the presenter.
 */
public class ViewLeaderboardInteractor implements ViewLeaderboardInputBoundary {
    private final ViewLeaderboardDataAccessInterface dataAccess;
    private final ViewLeaderboardOutputBoundary presenter;

    public ViewLeaderboardInteractor(ViewLeaderboardDataAccessInterface dataAccess,
                                     ViewLeaderboardOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        final List<Player> players;
        try {
            players = dataAccess.getAllPlayers();
        }
        catch (DataAccessException ex) {
            presenter.prepareFailView("Could not load the leaderboard: " + ex.getMessage());
            return;
        }

        final List<ViewLeaderboardOutputData.Row> rows = new ArrayList<>();
        int rank = 1;
        for (Player player : new Leaderboard(players).getRanking()) {
            rows.add(new ViewLeaderboardOutputData.Row(
                    rank, player.getUsername(), player.getWins(), player.getLosses()));
            rank++;
        }
        presenter.prepareSuccessView(new ViewLeaderboardOutputData(rows));
    }
}

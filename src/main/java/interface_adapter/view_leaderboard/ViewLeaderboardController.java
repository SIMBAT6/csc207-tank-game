package interface_adapter.view_leaderboard;

import use_case.view_leaderboard.ViewLeaderboardInputBoundary;

/**
 * Called when a player clicks Leaderboard on the main menu, or Refresh on the leaderboard screen.
 */
public class ViewLeaderboardController {
    private final ViewLeaderboardInputBoundary interactor;

    public ViewLeaderboardController(ViewLeaderboardInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute() {
        interactor.execute();
    }
}

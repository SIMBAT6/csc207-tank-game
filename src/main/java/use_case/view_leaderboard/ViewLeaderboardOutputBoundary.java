package use_case.view_leaderboard;

/**
 * Shows the leaderboard, or why it could not be loaded.
 */
public interface ViewLeaderboardOutputBoundary {
    void prepareSuccessView(ViewLeaderboardOutputData outputData);

    void prepareFailView(String errorMessage);
}

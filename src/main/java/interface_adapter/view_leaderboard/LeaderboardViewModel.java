package interface_adapter.view_leaderboard;

import interface_adapter.ViewModel;

/**
 * State of the leaderboard screen.
 */
public class LeaderboardViewModel extends ViewModel<LeaderboardState> {
    public static final String VIEW_NAME = "leaderboard";

    public LeaderboardViewModel() {
        super(VIEW_NAME);
        setState(new LeaderboardState());
    }
}

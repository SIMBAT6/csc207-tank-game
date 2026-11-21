package interface_adapter.end_round;

import interface_adapter.ViewModel;

/**
 * Score bar and round-over banner drawn on top of the game screen.
 */
public class EndRoundViewModel extends ViewModel<EndRoundState> {
    public static final String VIEW_NAME = "end round";

    public EndRoundViewModel() {
        super(VIEW_NAME);
        setState(new EndRoundState());
    }
}

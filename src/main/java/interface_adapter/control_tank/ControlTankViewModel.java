package interface_adapter.control_tank;

import interface_adapter.ViewModel;

/**
 * Control Tank's part of the game screen. Fires "state" after every tick and
 * {@link #ROUND_OVER} once when a round ends.
 */
public final class ControlTankViewModel extends ViewModel<ControlTankState> {
    /** Property fired once per round, on the first tick that fewer than two tanks are alive. */
    public static final String ROUND_OVER = "roundOver";

    public ControlTankViewModel() {
        super("control tank");
        setState(new ControlTankState());
    }
}

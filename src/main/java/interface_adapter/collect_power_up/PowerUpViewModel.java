package interface_adapter.collect_power_up;

import interface_adapter.ViewModel;

/**
 * View model for the power-up icons and HUD labels on the game screen (UC4).
 * GameView listens to it and redraws when the presenter fires a change.
 */
public class PowerUpViewModel extends ViewModel<PowerUpState> {

    public static final String VIEW_NAME = "power ups";

    public PowerUpViewModel() {
        super(VIEW_NAME);
        setState(new PowerUpState());
    }
}

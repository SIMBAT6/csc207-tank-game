package interface_adapter.collect_power_up;

import java.util.ArrayList;
import java.util.List;

import use_case.collect_power_up.CollectPowerUpOutputBoundary;
import use_case.collect_power_up.CollectPowerUpOutputData;

/** Turns UC4 output data into display-ready text and positions. */
public class CollectPowerUpPresenter implements CollectPowerUpOutputBoundary {

    private static final int TICKS_PER_SECOND = 60;

    private final PowerUpViewModel viewModel;

    public CollectPowerUpPresenter(PowerUpViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(CollectPowerUpOutputData outputData) {
        final List<PowerUpState.Icon> icons = new ArrayList<>();
        for (CollectPowerUpOutputData.MapItem item : outputData.getMapItems()) {
            icons.add(new PowerUpState.Icon(prettyName(item.getType()), item.getX(), item.getY()));
        }

        final List<String> hud = new ArrayList<>();
        final List<String> active = outputData.getActivePowerUps();
        final List<Integer> remaining = outputData.getRemainingTicks();
        for (int i = 0; i < active.size(); i++) {
            if (active.get(i).isEmpty()) {
                hud.add("");
            }
            else {
                final int seconds = (remaining.get(i) + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
                hud.add(prettyName(active.get(i)) + " (" + seconds + "s)");
            }
        }

        final PowerUpState state = viewModel.getState();
        state.setIcons(icons);
        state.setHudLabels(hud);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    /** "SHOTGUN" -> "Shotgun". */
    static String prettyName(String type) {
        if (type.isEmpty()) {
            return type;
        }
        return type.charAt(0) + type.substring(1).toLowerCase();
    }
}

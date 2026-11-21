package interface_adapter.collect_power_up;

import use_case.collect_power_up.CollectPowerUpInputBoundary;
import use_case.collect_power_up.CollectPowerUpInputData;

/** Called by the game loop (UC3's Swing Timer) once per tick. */
public class CollectPowerUpController {

    private final CollectPowerUpInputBoundary interactor;

    public CollectPowerUpController(CollectPowerUpInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(long tick) {
        interactor.execute(new CollectPowerUpInputData(tick));
    }
}

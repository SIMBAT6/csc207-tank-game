package interface_adapter.control_tank;

import use_case.control_tank.ControlTankInputBoundary;
import use_case.control_tank.ControlTankInputData;

import java.util.Set;

/**
 * Called by the game screen every tick with the keys held down.
 */
public final class ControlTankController {
    private final ControlTankInputBoundary interactor;
    private final KeyBindings player1Keys;
    private final KeyBindings player2Keys;

    public ControlTankController(ControlTankInputBoundary interactor,
                                 KeyBindings player1Keys, KeyBindings player2Keys) {
        this.interactor = interactor;
        this.player1Keys = player1Keys;
        this.player2Keys = player2Keys;
    }

    public void execute(Set<Integer> pressedKeys) {
        interactor.execute(new ControlTankInputData(
                player1Keys.toCommand(pressedKeys), player2Keys.toCommand(pressedKeys)));
    }
}

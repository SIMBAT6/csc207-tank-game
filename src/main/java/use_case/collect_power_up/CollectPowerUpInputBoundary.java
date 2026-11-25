package use_case.collect_power_up;

/** Input boundary for UC4. Called once per game tick by the game loop. */
public interface CollectPowerUpInputBoundary {

    void execute(CollectPowerUpInputData inputData);
}

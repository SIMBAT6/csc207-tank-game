package use_case.collect_power_up;

/** Output boundary for UC4, implemented by the presenter. */
public interface CollectPowerUpOutputBoundary {

    /** Called every tick with the latest power-up state (map items + each player's HUD). */
    void prepareSuccessView(CollectPowerUpOutputData outputData);
}

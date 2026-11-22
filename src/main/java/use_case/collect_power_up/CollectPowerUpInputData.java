package use_case.collect_power_up;

/** Input data for UC4: the number of the current game tick in this round. */
public class CollectPowerUpInputData {

    private final long tick;

    public CollectPowerUpInputData(long tick) {
        this.tick = tick;
    }

    public long getTick() {
        return tick;
    }
}

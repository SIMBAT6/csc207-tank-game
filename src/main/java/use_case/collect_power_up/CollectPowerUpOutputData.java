package use_case.collect_power_up;

import java.util.ArrayList;
import java.util.List;

/**
 * Output data for UC4. Uses only plain values (Strings, numbers) so the presenter
 * and view never depend on entity classes.
 */
public class CollectPowerUpOutputData {

    /** One power-up lying on the map. */
    public static class MapItem {
        private final String type;
        private final double x;
        private final double y;

        public MapItem(String type, double x, double y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public String getType() {
            return type;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    private final List<MapItem> mapItems;
    private final List<String> activePowerUps;
    private final List<Integer> remainingTicks;
    private final int collectedByPlayer;

    /**
     * @param mapItems          power-ups currently on the map
     * @param activePowerUps    per player, the name of the held power-up or "" if none
     * @param remainingTicks    per player, ticks left on the held power-up (0 if none)
     * @param collectedByPlayer index of the player who picked one up this tick, or -1
     */
    public CollectPowerUpOutputData(List<MapItem> mapItems, List<String> activePowerUps,
                                    List<Integer> remainingTicks, int collectedByPlayer) {
        this.mapItems = new ArrayList<>(mapItems);
        this.activePowerUps = new ArrayList<>(activePowerUps);
        this.remainingTicks = new ArrayList<>(remainingTicks);
        this.collectedByPlayer = collectedByPlayer;
    }

    public List<MapItem> getMapItems() {
        return new ArrayList<>(mapItems);
    }

    public List<String> getActivePowerUps() {
        return new ArrayList<>(activePowerUps);
    }

    public List<Integer> getRemainingTicks() {
        return new ArrayList<>(remainingTicks);
    }

    public int getCollectedByPlayer() {
        return collectedByPlayer;
    }
}

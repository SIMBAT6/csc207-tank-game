package interface_adapter.collect_power_up;

import java.util.ArrayList;
import java.util.List;

/** What the game screen needs to draw power-ups and the HUD. */
public class PowerUpState {

    /** One power-up icon to draw on the map. */
    public static class Icon {
        private final String label;
        private final double x;
        private final double y;

        public Icon(String label, double x, double y) {
            this.label = label;
            this.x = x;
            this.y = y;
        }

        public String getLabel() {
            return label;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    private List<Icon> icons = new ArrayList<>();
    /** Per player, e.g. "Shotgun (8s)" or "" when no power-up. */
    private List<String> hudLabels = new ArrayList<>();
    /** Per player, the laser aim line's points {x, y}; empty when there is none. */
    private List<List<double[]>> aimPaths = new ArrayList<>();

    public List<Icon> getIcons() {
        return new ArrayList<>(icons);
    }

    public void setIcons(List<Icon> icons) {
        this.icons = new ArrayList<>(icons);
    }

    public List<String> getHudLabels() {
        return new ArrayList<>(hudLabels);
    }

    public void setHudLabels(List<String> hudLabels) {
        this.hudLabels = new ArrayList<>(hudLabels);
    }

    public List<List<double[]>> getAimPaths() {
        return new ArrayList<>(aimPaths);
    }

    public void setAimPaths(List<List<double[]>> aimPaths) {
        this.aimPaths = new ArrayList<>(aimPaths);
    }
}

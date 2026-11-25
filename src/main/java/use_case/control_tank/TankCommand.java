package use_case.control_tank;

/**
 * What one player wants their tank to do this tick, already translated from keys.
 */
public final class TankCommand {
    /** Do nothing this tick. */
    public static final TankCommand NONE = new TankCommand(0, 0, false);

    private final int drive;
    private final int turn;
    private final boolean fire;

    /**
     * @param drive 1 forward, -1 backward, 0 stay
     * @param turn  1 clockwise, -1 counter-clockwise, 0 keep the angle
     * @param fire  whether the fire key is held
     */
    public TankCommand(int drive, int turn, boolean fire) {
        this.drive = Integer.signum(drive);
        this.turn = Integer.signum(turn);
        this.fire = fire;
    }

    public int getDrive() {
        return drive;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isFire() {
        return fire;
    }
}

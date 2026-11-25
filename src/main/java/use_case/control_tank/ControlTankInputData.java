package use_case.control_tank;

/**
 * Both players' commands for one tick.
 */
public final class ControlTankInputData {
    private final TankCommand player1;
    private final TankCommand player2;

    public ControlTankInputData(TankCommand player1, TankCommand player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public TankCommand getPlayer1() {
        return player1;
    }

    public TankCommand getPlayer2() {
        return player2;
    }
}

package use_case.end_round;

/**
 * Who survived the round, as reported by Control Tank (UC3).
 */
public class EndRoundInputData {
    private final int winner;

    /**
     * @param winner {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.DRAW}
     */
    public EndRoundInputData(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }
}

package use_case.end_round;

/**
 * The result of a round, as plain values.
 */
public class EndRoundOutputData {
    private final int roundWinner;
    private final String roundWinnerName;
    private final int score1;
    private final int score2;
    private final int matchWinner;
    private final String matchWinnerName;
    private final boolean resultsSaved;

    /**
     * @param roundWinner     {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.DRAW}
     * @param roundWinnerName the round winner's username, or null on a draw
     * @param score1          player 1's round wins so far
     * @param score2          player 2's round wins so far
     * @param matchWinner     {@code Match.PLAYER_1}, {@code Match.PLAYER_2} or {@code Match.NO_WINNER}
     * @param matchWinnerName the match winner's username, or null while the match goes on
     * @param resultsSaved    false if a logged-in player's result could not be saved
     */
    public EndRoundOutputData(int roundWinner, String roundWinnerName, int score1, int score2,
                              int matchWinner, String matchWinnerName, boolean resultsSaved) {
        this.roundWinner = roundWinner;
        this.roundWinnerName = roundWinnerName;
        this.score1 = score1;
        this.score2 = score2;
        this.matchWinner = matchWinner;
        this.matchWinnerName = matchWinnerName;
        this.resultsSaved = resultsSaved;
    }

    public int getRoundWinner() {
        return roundWinner;
    }

    public String getRoundWinnerName() {
        return roundWinnerName;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int getMatchWinner() {
        return matchWinner;
    }

    public String getMatchWinnerName() {
        return matchWinnerName;
    }

    public boolean isResultsSaved() {
        return resultsSaved;
    }
}

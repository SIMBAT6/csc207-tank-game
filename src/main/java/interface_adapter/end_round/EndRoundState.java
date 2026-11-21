package interface_adapter.end_round;

/**
 * What the game screen shows about the score and the end of a round.
 */
public class EndRoundState {
    private int score1;
    private int score2;
    private String banner;
    private String warning;
    private boolean matchOver;

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    /** The big message in the middle of the screen, or null when there is none. */
    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    /** A warning under the banner (e.g. results not saved), or null. */
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }
}

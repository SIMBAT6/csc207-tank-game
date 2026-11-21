package use_case.start_match;

public enum MatchFormat {
    BO1(1),
    BO3(2),
    BO5(3);

    private final int winsToWin;

    MatchFormat(int winsToWin) {
        this.winsToWin = winsToWin;
    }

    public int getWinsToWin() {
        return winsToWin;
    }
}
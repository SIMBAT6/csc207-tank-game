package entity;

/**
 * A person playing the game, either logged in to an account or playing as a guest.
 */
public class Player {
    private final String username;
    private final boolean guest;
    private int wins;
    private int losses;

    public Player(String username, boolean guest, int wins, int losses) {
        this.username = username;
        this.guest = guest;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public boolean isGuest() {
        return guest;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public void recordWin() {
        wins++;
    }

    public void recordLoss() {
        losses++;
    }
}

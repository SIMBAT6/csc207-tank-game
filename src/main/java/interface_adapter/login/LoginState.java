package interface_adapter.login;

public final class LoginState {
    private String player1Name = "Guest 1";
    private String player2Name = "Guest 2";
    private String message = " ";

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getMessage() {
        return message;
    }

    void setPlayerName(int side, String name) {
        if (side == 1) {
            player1Name = name;
        } else {
            player2Name = name;
        }
    }

    void setMessage(String message) {
        this.message = message;
    }
}

package use_case.login;

public final class LoginInputData {
    private final String username;
    private final char[] password;
    private final int side;

    public LoginInputData(String username, char[] password, int side) {
        if (side != 1 && side != 2) {
            throw new IllegalArgumentException("Choose Player 1 or Player 2.");
        }
        this.username = username.strip();
        this.password = password;
        this.side = side;
    }

    public String getUsername() {
        return username;
    }

    /** The caller owns this array and clears it after the use case finishes. */
    public char[] getPassword() {
        return password;
    }

    public int getSide() {
        return side;
    }
}

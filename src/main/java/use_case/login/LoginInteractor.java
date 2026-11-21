package use_case.login;

import entity.Player;
import use_case.session.CurrentPlayerDataAccessInterface;

import java.io.IOException;
import java.util.Optional;

public final class LoginInteractor implements LoginInputBoundary {
    private final UserDataAccessInterface users;
    private final CurrentPlayerDataAccessInterface currentPlayers;
    private final LoginOutputBoundary presenter;
    private final PasswordHasher passwords = new PasswordHasher();

    public LoginInteractor(UserDataAccessInterface users,
                           CurrentPlayerDataAccessInterface currentPlayers,
                           LoginOutputBoundary presenter) {
        this.users = users;
        this.currentPlayers = currentPlayers;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoginInputData input) {
        if (!hasCredentials(input)) {
            return;
        }
        try {
            Optional<String> storedHash = users.findPasswordHash(input.getUsername());
            if (storedHash.isEmpty() || !passwords.matches(input.getPassword(), storedHash.get())) {
                presenter.prepareFailure("Incorrect username or password.");
                return;
            }
            selectPlayer(input.getSide(), new Player(input.getUsername(), false, 0, 0));
        } catch (IOException error) {
            presenter.prepareFailure("Could not read saved accounts. You can continue as a guest.");
        }
    }

    @Override
    public void signUp(LoginInputData input) {
        if (!hasCredentials(input)) {
            return;
        }
        try {
            if (!users.createUser(input.getUsername(), passwords.hash(input.getPassword()))) {
                presenter.prepareFailure("That username is already taken.");
                return;
            }
            selectPlayer(input.getSide(), new Player(input.getUsername(), false, 0, 0));
        } catch (IOException error) {
            presenter.prepareFailure("Could not save the account. You can continue as a guest.");
        }
    }

    @Override
    public void continueAsGuest(int side) {
        selectPlayer(side, new Player("Guest " + side, true, 0, 0));
    }

    private boolean hasCredentials(LoginInputData input) {
        if (input.getUsername().isEmpty() || input.getPassword().length == 0) {
            presenter.prepareFailure("Enter a username and password.");
            return false;
        }
        return true;
    }

    private void selectPlayer(int side, Player player) {
        currentPlayers.save(side, player);
        presenter.prepareSuccess(player.getUsername(), side);
    }
}

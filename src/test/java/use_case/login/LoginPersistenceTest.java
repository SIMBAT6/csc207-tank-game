package use_case.login;

import data_access.FileUserDataAccessObject;
import data_access.InMemoryCurrentPlayerDataAccessObject;
import entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LoginPersistenceTest {
    @TempDir
    Path directory;

    @Test
    void savedAccountCanBeUsedAfterRestartWithoutChangingOtherSide() throws Exception {
        Path file = directory.resolve("accounts.properties");
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryCurrentPlayerDataAccessObject originalPlayers =
                new InMemoryCurrentPlayerDataAccessObject();
        LoginInteractor firstRun = new LoginInteractor(
                new FileUserDataAccessObject(file), originalPlayers, presenter);

        firstRun.signUp(input("correct-password", 1));
        assertEquals("alice", originalPlayers.findBySide(1).orElseThrow().getUsername());
        assertFalse(Files.readString(file).contains("correct-password"));

        InMemoryCurrentPlayerDataAccessObject restartedPlayers =
                new InMemoryCurrentPlayerDataAccessObject();
        LoginInteractor secondRun = new LoginInteractor(
                new FileUserDataAccessObject(file), restartedPlayers, presenter);
        secondRun.continueAsGuest(1);
        Player guest = restartedPlayers.findBySide(1).orElseThrow();
        secondRun.execute(input("correct-password", 2));
        Player alice = restartedPlayers.findBySide(2).orElseThrow();
        assertEquals("alice", alice.getUsername());
        assertFalse(alice.isGuest());
        assertSame(guest, restartedPlayers.findBySide(1).orElseThrow());

        secondRun.execute(input("wrong-password", 2));
        assertEquals("Incorrect username or password.", presenter.message);
        assertSame(alice, restartedPlayers.findBySide(2).orElseThrow());

        secondRun.signUp(input("another-password", 1));
        assertEquals("That username is already taken.", presenter.message);
        assertSame(guest, restartedPlayers.findBySide(1).orElseThrow());
    }

    private LoginInputData input(String password, int side) {
        return new LoginInputData("alice", password.toCharArray(), side);
    }

    private static final class RecordingPresenter implements LoginOutputBoundary {
        private String message;

        @Override
        public void prepareSuccess(String username, int side) {
            message = null;
        }

        @Override
        public void prepareFailure(String message) {
            this.message = message;
        }
    }
}

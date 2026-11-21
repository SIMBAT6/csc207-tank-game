package use_case.login;

import java.io.IOException;
import java.util.Optional;

public interface UserDataAccessInterface {
    Optional<String> findPasswordHash(String username) throws IOException;

    /** Returns false if the username is already registered. */
    boolean createUser(String username, String passwordHash) throws IOException;
}

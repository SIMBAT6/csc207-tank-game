package data_access;

import use_case.login.UserDataAccessInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Properties;

public final class FileUserDataAccessObject implements UserDataAccessInterface {
    private final Path file;

    public FileUserDataAccessObject(Path file) {
        this.file = file.toAbsolutePath().normalize();
    }

    @Override
    public Optional<String> findPasswordHash(String username) throws IOException {
        return Optional.ofNullable(readAccounts(file).getProperty(username));
    }

    @Override
    public boolean createUser(String username, String passwordHash) throws IOException {
        synchronized (FileUserDataAccessObject.class) {
            return writeAccount(file, username, passwordHash);
        }
    }

    private static boolean writeAccount(Path file, String username, String passwordHash)
            throws IOException {
        Path directory = Files.createDirectories(file.getParent());
        Path lockFile = file.resolveSibling(file.getFileName() + ".lock");
        try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
             FileLock lock = channel.lock()) {
            Properties accounts = readAccounts(file);
            if (accounts.containsKey(username)) {
                return false;
            }

            accounts.setProperty(username, passwordHash);
            Path temporary = Files.createTempFile(directory, "accounts-", ".tmp");
            try {
                try (OutputStream output = Files.newOutputStream(temporary)) {
                    accounts.store(output, "Ricochet Tanks accounts");
                }
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporary);
            }
            return true;
        }
    }

    private static Properties readAccounts(Path file) throws IOException {
        Properties accounts = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            accounts.load(input);
        } catch (NoSuchFileException absent) {
            // The first signup creates the account file.
        } catch (IllegalArgumentException corruptFile) {
            throw new IOException("Invalid account file", corruptFile);
        }
        return accounts;
    }
}

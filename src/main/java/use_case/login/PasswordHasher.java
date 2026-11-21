package use_case.login;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordHasher {
    private static final int ITERATIONS = 600_000;
    private final SecureRandom random = new SecureRandom();

    String hash(char[] password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + ":" + encoder.encodeToString(derive(password, salt));
    }

    boolean matches(char[] password, String storedHash) {
        String[] parts = storedHash.split(":", -1);
        if (parts.length != 2) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] salt = decoder.decode(parts[0]);
            byte[] expected = decoder.decode(parts[1]);
            return salt.length == 16 && expected.length == 32
                    && MessageDigest.isEqual(expected, derive(password, salt));
        } catch (IllegalArgumentException invalidEncoding) {
            return false;
        }
    }

    private byte[] derive(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException unavailable) {
            throw new IllegalStateException("Password hashing is unavailable", unavailable);
        } finally {
            spec.clearPassword();
        }
    }
}

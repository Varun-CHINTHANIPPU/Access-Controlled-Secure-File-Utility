package com.securefile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


public class PasswordHasher {

    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);

    public static String hashPassword(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static boolean checkPassword(char[] password, String storedHash, byte[] salt) {
        String calculated = hashPassword(password, salt);
        return calculated.equals(storedHash);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return salt;
    }

    public static boolean verifyPassword(char[] password, String storedSaltBase64, String storedHashBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(storedSaltBase64);
            String calculatedHash = hashPassword(password, salt);
            return calculatedHash.equals(storedHashBase64);
        } catch (Exception e) {
            logger.error("Password verification failed due to error", e);
            return false;
        }
    }
}
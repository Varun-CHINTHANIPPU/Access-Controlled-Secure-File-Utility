package com.securefile;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();
        String filePath = args[1];

        List<User> users = ConfigManager.loadUsers();
        if (users.isEmpty()) {
            logger.error("No users configured. Cannot proceed.");
            return;
        }

        // Login
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Username: ");
        String username = reader.readLine().trim();

        System.out.print("Password: ");
        String password = reader.readLine().trim();

        User user = users.stream()
                .filter(u -> u.username().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null || !PasswordHasher.verifyPassword(password.toCharArray(), user.salt(), user.passwordHash())) {
            logger.warn("Authentication failed for user: {}", username);
            System.out.println("Login failed.");
            return;
        }

        logger.info("Login successful for {}", username);

        // Permission check
        if (!user.allowedFiles().contains(filePath)) {
            logger.warn("Access denied: {} cannot operate on {}", username, filePath);
            System.out.println("Access denied - file not in your allowed list.");
            return;
        }

        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try {
            if (command.equals("encrypt")) {
                File outputFile = new File(filePath + ".encrypted");
                CryptoService.encryptFile(inputFile, outputFile, deriveKey(user, password));
                System.out.println("Encrypted to: " + outputFile.getName());
            } else if (command.equals("decrypt")) {
                if (!filePath.endsWith(".encrypted")) {
                    System.out.println("File does not appear to be encrypted (.encrypted suffix missing)");
                    return;
                }
                File outputFile = new File(filePath.replace(".encrypted", ""));
                CryptoService.decryptFile(inputFile, outputFile, deriveKey(user, password));
                System.out.println("Decrypted to: " + outputFile.getName());
            } else {
                printUsage();
            }
        } catch (Exception e) {
            logger.error("Operation failed", e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static byte[] deriveKey(User user, String password) {
        byte[] salt = Base64.getDecoder().decode(user.salt());
        return CryptoService.deriveKeyFromPassword(password.toCharArray(), salt);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -jar secure-file-utility.jar encrypt <file>");
        System.out.println("  java -jar secure-file-utility.jar decrypt <file.encrypted>");
    }
}
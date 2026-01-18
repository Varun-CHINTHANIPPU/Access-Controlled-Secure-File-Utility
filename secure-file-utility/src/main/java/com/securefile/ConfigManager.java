package com.securefile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {

    private static final String CONFIG_PATH = "/users.json";

    public static List<User> loadUsers() throws IOException {
        InputStream is = ConfigManager.class.getResourceAsStream(CONFIG_PATH);
        if (is == null) {
            System.err.println("Config file not found in resources: " + CONFIG_PATH);
            return List.of();
        }

        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        is.close();

        List<User> users = new ArrayList<>();

        json = json.trim();
        if (json.equals("[]") || json.isEmpty()) {
            return users;
        }

        String content = json.substring(1, json.length() - 1).trim();


        String[] rawObjects = content.split("(?<=\\}),\\s*(?=\\{)");

        for (String rawObj : rawObjects) {
            String obj = rawObj.trim().replace("{", "").replace("}", "").trim();

            String username = extractValue(obj, "\"username\":");
            String salt = extractValue(obj, "\"salt\":");
            String passwordHash = extractValue(obj, "\"passwordHash\":");
            String filesStr = extractArray(obj, "\"allowedFiles\":");

            if (username != null && !username.isEmpty() && passwordHash != null) {
                List<String> allowedFiles = filesStr.isEmpty()
                        ? List.of()
                        : Arrays.stream(filesStr.split(","))
                        .map(s -> s.trim().replace("\"", ""))
                        .filter(s -> !s.isEmpty())
                        .toList();

                users.add(new User(username, salt, passwordHash, allowedFiles));
            }
        }

        return users;
    }


    private static String extractValue(String obj, String key) {
        int start = obj.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int quoteStart = obj.indexOf("\"", start);
        if (quoteStart == -1) return null;
        quoteStart++;
        int quoteEnd = obj.indexOf("\"", quoteStart);
        if (quoteEnd == -1) return null;
        return obj.substring(quoteStart, quoteEnd);
    }

    private static String extractArray(String obj, String key) {
        int start = obj.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int arrayStart = obj.indexOf("[", start);
        if (arrayStart == -1) return "";
        int arrayEnd = obj.indexOf("]", arrayStart);
        if (arrayEnd == -1) return "";
        return obj.substring(arrayStart + 1, arrayEnd).trim();
    }

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    public static void main(String[] args) throws IOException {
        List<User> users = loadUsers();
        logger.info("Loaded {} users from config", users.size());

        for (User u : users) {
            logger.info(" - {} can access: {}", u.username(), u.allowedFiles());
        }
    }
}
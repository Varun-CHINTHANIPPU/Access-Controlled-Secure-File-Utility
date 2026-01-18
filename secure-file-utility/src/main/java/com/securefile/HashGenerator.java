package com.securefile;

import java.util.Base64;

public class HashGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java HashGenerator <password1> <password2> ...");
            System.out.println("Example: java HashGenerator mysecret123 password456");
            return;
        }

        for (String plainPassword : args) {
            byte[] salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hashPassword(plainPassword.toCharArray(), salt);

            System.out.println("Password: " + plainPassword);
            System.out.println("Salt (Base64): " + Base64.getEncoder().encodeToString(salt));
            System.out.println("Hash (Base64): " + hash);
            System.out.println("→ Paste into users.json: \"passwordHash\": \"" + hash + "\"");
            System.out.println("---");
        }
    }
}
package com.securefile;

import java.util.List;

public record User(
        String username,
        String salt,
        String passwordHash,
        List<String> allowedFiles
) {
      public User(String username, String salt, String passwordHash) {
        this(username, salt, passwordHash, List.of());
    }
}
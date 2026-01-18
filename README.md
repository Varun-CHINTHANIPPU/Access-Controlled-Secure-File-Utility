# Access-Controlled-Secure-File-Utility

A command-line Java tool that lets authorized users **encrypt** and **decrypt** files using **AES-256-GCM** with **PBKDF2** password-based key derivation.  
Includes real user authentication, per-user file permissions, and audit logging.

Built with **Java 25** and **Maven** — zero external crypto libraries.

## Features

- Secure password authentication with PBKDF2 + random salt
- User-based access control (each user has allowed files list)
- AES-256-GCM authenticated encryption (confidentiality + integrity)
- Streaming encryption/decryption (handles large files)
- Professional logging with SLF4J + Logback
- Executable fat JAR — run anywhere with one command

## Tech Stack

- Java 25 (latest LTS)
- Maven (build & dependencies)
- Built-in Java Cryptography Architecture (JCA): AES/GCM, PBKDF2
- SLF4J + Logback (logging)
- No external crypto libs — fully secure & portable

## Installation / Build

1. Clone the repo
2. Build the executable JAR
   
```
mvn clean package
```
4. usage
```
 java -jar target/secure-file-utility-1.0-SNAPSHOT-jar-with-dependencies.jar <command> <file>
 ```

Commands:

```encrypt <file>           Encrypts file → creates file.encrypted```

```decrypt <file.encrypted> # Decrypts → creates original file ```

##Configuration

Users and permissions are stored in src/main/resources/users.json:
```
JSON[
  {
    "username": "alice",
    "salt": "gU3nZiuE/3PmLVcanET1eA==",
    "passwordHash": "kddUjmZreLd9vtAufPZBep0whlkt5pljyTbUYp9mKcw=",
    "allowedFiles": ["budget.xlsx", "secret.txt"]
  },
  {
    "username": "bob",
    "salt": "V0G8GK12DWNyrpC9GPejag==",
    "passwordHash": "ZKFexWP0Jv8iXTsLWVokNiYRAwZQ7Z5RD1oGpYMcI2Y=",
    "allowedFiles": ["notes.txt"]
  }
]

passwordHash — PBKDF2 hash (Base64)
salt — random salt (Base64)
allowedFiles — list of files the user can encrypt/decrypt
```
## Security Notes

- Passwords are never stored in plain text
- Each user has unique random salt
-AES-256-GCM provides both confidentiality and integrity
- No external crypto libraries — uses Java’s built-in JCA (secure & FIPS-capable)
- For production use: consider Argon2 instead of PBKDF2, add secure delete, etc.


## Future Improvements

- Argon2 key derivation
- Secure file deletion after encryption
- CLI menu for interactive mode
- Unit tests (JUnit 5)
- Overwrite protection & confirmation prompts

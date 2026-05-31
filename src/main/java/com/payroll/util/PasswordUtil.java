package com.payroll.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String PREFIX = "PBKDF2$";

    private PasswordUtil() {
        // Utility class
    }

    public static String hashPassword(String password) {
        if (password == null) {
            password = "";
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] derivedKey = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return PREFIX + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derivedKey);
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        if (!storedPassword.startsWith(PREFIX)) {
            return password != null && storedPassword.equals(password);
        }

        try {
            String[] parts = storedPassword.substring(PREFIX.length()).split("\\$");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);

            byte[] testHash = pbkdf2(password != null ? password.toCharArray() : new char[0], salt, iterations, hash.length * 8);
            return slowEquals(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to hash password", e);
        }
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}

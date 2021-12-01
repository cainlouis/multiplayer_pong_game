package com.almasb.fxglgames.pong;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingSHA3 {
    private static final String algorithm = "SHA3-256";
    public HashingSHA3(){

    }

    /**
     * ShA3-256 hashing.
     * Possible algorithm choices includes "SHA-256" "SHA-512",
     * "SHA3-256" and "SHA3-512".
     */

    public static byte[] computeHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        return hashbytes;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

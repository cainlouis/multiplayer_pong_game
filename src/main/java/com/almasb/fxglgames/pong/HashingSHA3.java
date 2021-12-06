package com.almasb.fxglgames.pong;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashingSHA3 takes the password and hashes it using the SHA3-256 algorithm
 * @author Rodrigo Rivas Alfaro
 */
public class HashingSHA3 {
    private static final String ALGORITHM = "SHA3-256";

    /**
     * SHA3-256 hashing. Possible algorithm choices includes "SHA-256" "SHA-512",
     * "SHA3-256" and "SHA3-512".
     * @param password
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] computeHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * This method converts byte to hexadecimal string 
     * @param bytes
     * @return 
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

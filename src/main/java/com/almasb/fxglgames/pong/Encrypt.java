package com.almasb.fxglgames.pong;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encrypt {
    private static final String algorithm = "AES/GCM/NoPadding";

    //Method to encrypt file using asymmetric key crypto
    public static void encryptFile(SecretKey secretKey, File inputFile, File outputFile)
            throws Exception{

        //Initializes the Cipher
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileOutputStream outputStream;
        //Create output stream
        try (
                FileInputStream inputStream = new FileInputStream(inputFile)) {
            //Create output stream
            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[64];
            int bytesRead;
            //Read up to 64 bytes of data at a time
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                //Cipher.update method takes byte array, input offset and input length
                byte[] output = encryptCipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    //Write the ciphertext for the buffer to the output file
                    outputStream.write(output);
                }
            }   //Encrypt the last buffer of plaintext
            byte[] output = encryptCipher.doFinal();
            if (output != null) {
                outputStream.write(output);
            }
            //Close the input and output streams
        }
        outputStream.close();
    }


    //Method to encrypt file using asymmetric key crypto
    public static void decryptFile(SecretKey secretKey, File inputFile, File outputFile)
            throws Exception {

        //Initializes the Cipher
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileOutputStream outputStream;
        //Create output stream
        try (
                FileInputStream inputStream = new FileInputStream(inputFile)) {
            //Create output stream
            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[64];
            int bytesRead;
            //Read up to 64 bytes of data at a time
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                //Cipher.update method takes byte array, input offset and input length
                byte[] output = encryptCipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    //Write the ciphertext for the buffer to the output file
                    outputStream.write(output);
                }
            }   //Encrypt the last buffer of plaintext
            byte[] output = encryptCipher.doFinal();
            if (output != null) {
                outputStream.write(output);
            }
            //Close the input and output streams
        }
        outputStream.close();
    }


}

package com.almasb.fxglgames.pong;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

public class Encrypt {
    private static final String algorithm = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final Path IVPath = Paths.get("src", "main", "resources","keystore","IV.dat");


    //Method to encrypt file using asymmetric key crypto
    public static void encryptFile(SecretKey secretKey, File inputFile, File outputFile)
            throws Exception{

         if(Files.notExists(IVPath)){
             generateIV();
         }

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, readIV());
        //Initializes the Cipher
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

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

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, readIV());
        //Initializes the Cipher
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

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

    private static void generateIV() throws IOException {
        byte[] IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        Files.write(IVPath, IV);
    }

    private static byte[] readIV() throws IOException {
         return Files.readAllBytes(IVPath);
    }


}

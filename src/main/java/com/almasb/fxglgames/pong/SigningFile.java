package com.almasb.fxglgames.pong;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

public class SigningFile {

    private static final String algorithm = "SHA256withECDSA";

    /**
     * Method for generating digital signature.
     */
    public static void generateSignature (PrivateKey privatekey, Path path) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, IOException, SignatureException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");

        //Initialize the signature scheme
        sig.initSign(privatekey);

        //Compute the signature
        String message = new String(Files.readAllBytes(path));
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = sig.sign();
        if(Files.notExists(Paths.get("src", "main", "resources","PongApp.sig"))) {
            Files.write(Paths.get("src", "main", "resources", "PongApp.sig"), signature);
        }
    }


    /**
     * Method for verifying digital signature.
     */
    public static boolean verifySignature(PublicKey publickey, Path path)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, IOException, SignatureException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");

        //Initialize the signature verification scheme.
        sig.initVerify(publickey);

        String message = new String(Files.readAllBytes(path));
        //Compute the signature.
        sig.update(message.getBytes("UTF-8"));

        //Verify the signature.
        boolean validSignature = sig.verify(readSignature());

        if(validSignature) {
            System.out.println("\nSignature is valid");
        } else {
            System.out.println("\nSignature is NOT valid!!!");
        }

        return validSignature;
    }


    public static byte[] readSignature() throws IOException {
        return Files.readAllBytes(Paths.get("src", "main", "resources","PongApp.sig",""));

    }

}

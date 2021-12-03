package com.almasb.fxglgames.pong;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class SigningFile {

    private static final String algorithm = "SHA256withECDSA";

    /**
     * Method for generating digital signature.
     */
    public static byte[] generateSignature (PrivateKey privatekey, String message) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, UnsupportedEncodingException, SignatureException, UnsupportedEncodingException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");

        //Initialize the signature scheme
        sig.initSign(privatekey);

        //Compute the signature
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = sig.sign();

        return signature;
    }


    /**
     * Method for verifying digital signature.
     */
    public static boolean verifySignature(byte[] signature, PublicKey publickey, String message)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, UnsupportedEncodingException, SignatureException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");

        //Initialize the signature verification scheme.
        sig.initVerify(publickey);

        //Compute the signature.
        sig.update(message.getBytes("UTF-8"));

        //Verify the signature.
        boolean validSignature = sig.verify(signature);

        if(validSignature) {
            System.out.println("\nSignature is valid");
        } else {
            System.out.println("\nSignature is NOT valid!!!");
        }

        return validSignature;
    }

}

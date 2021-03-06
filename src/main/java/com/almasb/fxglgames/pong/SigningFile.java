package com.almasb.fxglgames.pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;

/**
 * SigningFile creates a digital signature and verifies keys using the signature
 * @author Rodrigo Rivas Alfaro
 */
public class SigningFile {

    private static final String ALGORITHM = "SHA256withECDSA";
    private static final Path PONGSIGNFILE = Paths.get("src", "main", "resources","PongApp.sig");

    /**
     * Method for generating digital signature.
     * @param privatekey
     * @param path
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException 
     */
    public static void generateSignature (PrivateKey privatekey, Path path) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, IOException, SignatureException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(ALGORITHM, "SunEC");

        //Initialize the signature scheme
        sig.initSign(privatekey);

        //Compute the signature
        String message = new String(Files.readAllBytes(path));
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = sig.sign();

        Files.write(PONGSIGNFILE, signature);

    }

    /**
     * Method for verifying digital signature.
     * @param publickey
     * @param path
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException 
     */
    public static boolean verifySignature(PublicKey publickey, Path path)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, IOException, SignatureException {

        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(ALGORITHM, "SunEC");

        //Initialize the signature verification scheme.
        sig.initVerify(publickey);

        String message = new String(Files.readAllBytes(path));
        //Compute the signature.
        sig.update(message.getBytes("UTF-8"));

        //Verify the signature.
        boolean validSignature = sig.verify(readSignature());

        if (validSignature) {
            getDialogService().showMessageBox("Signature is valid");
        } else {
            getDialogService().showMessageBox("Signature is NOT valid!!!");
        }

        return validSignature;
    }

    /**
     * readSignature() returns the bytes of the signed PongApp.java
     * @return
     * @throws IOException 
     */
    public static byte[] readSignature() throws IOException {
        return Files.readAllBytes(PONGSIGNFILE);
    }

}

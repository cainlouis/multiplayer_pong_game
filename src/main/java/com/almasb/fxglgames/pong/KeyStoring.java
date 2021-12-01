package com.almasb.fxglgames.pong;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.*;
import java.security.KeyStore.*;
import java.security.cert.Certificate;

public class KeyStoring {
    private KeyStore keyStore;
    private String password;
    private static final String KEY_PAIR_ALIAS = "myPairKey";
    private static final String SECRET_KEY = "secretKey";
    private static final  String dir = "src/main/resources/keystore/keystore.p12";
    private String [] cmdArg = {"keytool", "-genkeypair", "-alias", KEY_PAIR_ALIAS,  "-keyalg", "EC", "-keysize", "256", "-dname", "CN=pongKey", "-validity", "365", "-storetype", "PKCS12", "-keystore", dir, "-storepass", ""};
    public KeyStoring(String password){
        this.password = password;
        cmdArg[cmdArg.length-1] = password;
    }

    public void createStoredKeys(){
        try {
            // Create the private and public key using keytool command.
            ProcessBuilder processBuilder = new ProcessBuilder().command(cmdArg);
            processBuilder.start();
            //Create the secret key and store it in the keystore file
            saveSecretKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadKey() throws Exception {
        //loads the p12 file and checks if the hash matches with the keystore's hash.
        FileInputStream fi = new FileInputStream(dir);
        keyStore.load(fi, password.toCharArray());
        if(fi != null){
            fi.close();
        }
    }

    private void saveSecretKey() throws Exception {
        // Load the keystore saved in the keystore/keystore.p12
        LoadKey();
        // Generating the secret key to store it in the loaded file.
        SecretKey sk = GenerateKey();

        ProtectionParameter protParam = new PasswordProtection(password.toCharArray());
        // creates a new entry in the p12 file with the secret key
        SecretKeyEntry skEntry = new SecretKeyEntry(sk);
        keyStore.setEntry(SECRET_KEY, skEntry, protParam);
    }

//Generates a Secret key using AES Algorithm
    private SecretKey GenerateKey() throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); //Initialize the key generator
        SecretKey key = keyGen.generateKey(); //Generate the key
        return key;
    }

    /*Methods to get each key*/

    public PrivateKey GetPrivateKey() throws Exception {
        LoadKey();
        ProtectionParameter protParam = new PasswordProtection(password.toCharArray());
        PrivateKeyEntry pkEntry = (PrivateKeyEntry) keyStore.getEntry(KEY_PAIR_ALIAS, protParam);
        PrivateKey myPrivateKey = pkEntry.getPrivateKey();
        return myPrivateKey;
    }

    public PublicKey GetPublicKey() throws Exception {
        LoadKey();
        Certificate certificate = keyStore.getCertificate(KEY_PAIR_ALIAS);
        PublicKey publicKey = certificate.getPublicKey();
        return publicKey;
    }

    public SecretKey GetSecretKey() throws Exception {
        LoadKey();
        ProtectionParameter protParam = new PasswordProtection(password.toCharArray());
        SecretKeyEntry skEntry = (SecretKeyEntry) keyStore.getEntry(SECRET_KEY, protParam);
        SecretKey mySecretKey = skEntry.getSecretKey();
        return mySecretKey;
    }

}

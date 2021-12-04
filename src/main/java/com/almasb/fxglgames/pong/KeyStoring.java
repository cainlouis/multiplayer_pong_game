package com.almasb.fxglgames.pong;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.KeyStore.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoring {
    private KeyStore keyStore;
    private String hash;
    private static final String KEY_PAIR_ALIAS = "myPairKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String ALGORITHM = "PKCS12";
    private static final  String dir = "src/main/resources/keystore/keystore.p12";
    private String [] cmdArg = {"keytool", "-genkeypair", "-alias", KEY_PAIR_ALIAS,  "-keyalg", "EC", "-keysize", "256", "-dname", "CN=pongKey", "-validity", "365", "-storetype", "PKCS12", "-keystore", dir, "-storepass", ""};

    public KeyStoring(String hash) throws KeyStoreException {
        this.hash = hash;
        cmdArg[cmdArg.length-1] = hash;
        keyStore = KeyStore.getInstance(ALGORITHM);
    }

    // Run just the first time when the file does not exist
    public void createStoredKeys(){
        try {
            // Create the private and public key using keytool command.
            createPairKeys();

            //Create the secret key and store it in the keystore file
            saveSecretKey();

            // Might need clean the hash after the file is created.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    * This method creates a process builder to run they keytools command with all the parameters
    * to create a private and a public keys
    */
    private void createPairKeys() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder().command(cmdArg);
        Process pro = processBuilder.start();
        try (var reader = new BufferedReader(
                new InputStreamReader(pro.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    /*
    * This method checks if the keystore exists. If it exists it compares the provided
    * hash with the stored hash in the keystore and if they match loads the keystore object
    */
    public void LoadKey(String hash) throws IOException, CertificateException, NoSuchAlgorithmException {
        //loads the p12 file and checks if the hash matches with the keystore's hash.
        FileInputStream fi = new FileInputStream(dir);
        keyStore.load(fi, hash.toCharArray());

        if(fi != null){
            fi.close();
        }
    }


    private void saveSecretKey() throws Exception {
        // Load the keystore saved in the keystore/keystore.p12
        LoadKey(hash);
        // Generating the secret key to store it in the loaded file.
        SecretKey sk = GenerateKey();

        ProtectionParameter protParam = new PasswordProtection(hash.toCharArray());
        // creates a new entry in the p12 file with the secret key
        SecretKeyEntry skEntry = new SecretKeyEntry(sk);
        keyStore.setEntry(SECRET_KEY, skEntry, protParam);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dir);
            keyStore.store(fos, hash.toCharArray());
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

//Generates a Secret key using AES Algorithm
    private SecretKey GenerateKey() throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); //Initialize the key generator
        SecretKey key = keyGen.generateKey(); //Generate the key
        return key;
    }

    /*Methods to get each key*/

    public PrivateKey GetPrivateKey(String hash) throws Exception {
        LoadKey(hash);
        ProtectionParameter protParam = new PasswordProtection(hash.toCharArray());
        PrivateKeyEntry pkEntry = (PrivateKeyEntry) keyStore.getEntry(KEY_PAIR_ALIAS, protParam);
        PrivateKey myPrivateKey = pkEntry.getPrivateKey();
        return myPrivateKey;
    }

    public PublicKey GetPublicKey(String hash) throws Exception {
        LoadKey(hash);
        Certificate certificate = keyStore.getCertificate(KEY_PAIR_ALIAS);
        PublicKey publicKey = certificate.getPublicKey();
        return publicKey;
    }

    public SecretKey GetSecretKey(String hash) throws Exception {
        LoadKey(hash);
        ProtectionParameter protParam = new PasswordProtection(hash.toCharArray());
        SecretKeyEntry skEntry = (SecretKeyEntry) keyStore.getEntry(SECRET_KEY, protParam);
        SecretKey mySecretKey = skEntry.getSecretKey();
        return mySecretKey;
    }

}

package com.almasb.fxglgames.pong;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

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
            ProcessBuilder processBuilder = new ProcessBuilder().command(cmdArg);
            processBuilder.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadKey() throws Exception {
        FileInputStream fi = new FileInputStream(dir);
        keyStore.load(fi, password.toCharArray());
        if(fi != null){
            fi.close();
        }
    }

    public void saveSecretKey() throws Exception {
        SecretKey sk = GenerateKey();
        LoadKey();
        keyStore.setKeyEntry(SECRET_KEY, sk, password.toCharArray(), null);
    }


    private SecretKey GenerateKey() throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); //Initialize the key generator
        SecretKey key = keyGen.generateKey(); //Generate the key
        return key;
    }

}

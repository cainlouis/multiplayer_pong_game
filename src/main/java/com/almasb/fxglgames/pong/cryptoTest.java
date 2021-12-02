package com.almasb.fxglgames.pong;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;

public class cryptoTest {
    public static void main(String[] args) throws Exception {
        //getting the password from the user to create a hash.
        byte [] byteHash = HashingSHA3.computeHash("test12");
        // converting that hash into hexadecimal
        String hash = HashingSHA3.bytesToHex(byteHash);

        // create a keystore object containing the user's hash,
        // This should be created only once
        KeyStoring ks = new KeyStoring(hash);
//        ks.createStoredKeys();
//        System.out.println(ks.GetPrivateKey(hash).toString());
//        System.out.println(ks.GetPublicKey(hash).toString());
//        System.out.println(ks.GetSecretKey(hash).toString());

        //when the hash is different from the file
        try {
            byte [] byteHash2 = HashingSHA3.computeHash("test21");
            String hash2 = HashingSHA3.bytesToHex(byteHash2);
            System.out.println(ks.GetPublicKey(hash2).toString());
        }catch (Exception e){
            System.out.println("wrong password");
        }

        //Encrypt file
//        Encrypt.encryptFile(ks.GetSecretKey(hash), new File("src/main/resources/savedFiles/fileToEncrypt.txt"), new File("src/main/resources/savedFiles/fileEncrypted.txt"));
        Encrypt.decryptFile(ks.GetSecretKey(hash), new File("src/main/resources/savedFiles/fileEncrypted.txt"), new File("src/main/resources/savedFiles/fileDecrypted.txt"));

    }
}

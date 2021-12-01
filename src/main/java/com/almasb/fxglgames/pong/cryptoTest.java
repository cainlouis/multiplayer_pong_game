package com.almasb.fxglgames.pong;

import java.security.KeyStoreException;
import java.security.MessageDigest;

public class cryptoTest {
    public static void main(String[] args) throws Exception {

        byte [] byteHash = HashingSHA3.computeHash("test12");
        String hash = HashingSHA3.bytesToHex(byteHash);

        KeyStoring ks = new KeyStoring(hash);
        ks.createStoredKeys();
        System.out.println(ks.GetPrivateKey().toString());
        System.out.println(ks.GetPublicKey().toString());
        System.out.println(ks.GetSecretKey().toString());
    }
}

package com.almasb.fxglgames.pong;

import java.security.KeyStoreException;

public class cryptoTest {
    public static void main(String[] args) throws Exception {
        KeyStoring ks = new KeyStoring("test12");
        ks.createStoredKeys();
        System.out.println(ks.GetPrivateKey().toString());
        System.out.println(ks.GetPublicKey().toString());
        System.out.println(ks.GetSecretKey().toString());
    }
}

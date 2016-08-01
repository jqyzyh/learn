//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jqyzyh.learn;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MNTSSLSocketFactory {
    private static final String KEY_STORE_TYPE_BKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String keyStorePassword = "123456";
    private static final String trustStorePassword = "123456";

    public MNTSSLSocketFactory() {

    }

    public static SSLSocketFactory getSSLSocketFactory(Context contextaa) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        ByteArrayInputStream inputStream = null;
        TrustManagerFactory tmf = null;

        try {
            KeyStore inputStream2 = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            inputStream2.load(null, trustStorePassword.toCharArray());
            String kmf = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(kmf);
            tmf.init(inputStream2);
        } catch (Exception var33) {
            var33.printStackTrace();
        } finally {
            if(null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException var30) {
                    var30.printStackTrace();
                }
            }

        }

        ByteArrayInputStream inputStream21 = null;
        KeyManagerFactory kmf1 = null;

        try {
            KeyStore socketFactory = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            socketFactory.load(null, keyStorePassword.toCharArray());
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            kmf1 = KeyManagerFactory.getInstance(algorithm);
            kmf1.init(socketFactory, keyStorePassword.toCharArray());
        } catch (Exception var32) {
            var32.printStackTrace();
        } finally {
            if(null != inputStream21) {
                try {
                    inputStream21.close();
                } catch (IOException var29) {
                    var29.printStackTrace();
                }
            }

        }

        try {
            context.init(kmf1.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyManagementException var31) {
            var31.printStackTrace();
        }

        SSLSocketFactory socketFactory1 = context.getSocketFactory();
        return socketFactory1;
    }
}

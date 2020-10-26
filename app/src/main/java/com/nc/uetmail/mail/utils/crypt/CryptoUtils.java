package com.nc.uetmail.mail.utils.crypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CryptoUtils {
    public static final String ENCRYPT_AES = "AES";

    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }
    // AES secret key
    public static SecretKey getAESKey(int keysize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPT_AES);
        keyGen.init(keysize);
        return keyGen.generateKey();
    }
    // AES secret key from bytes
    public static SecretKey bytes2AESKey(byte[] secretBytes) {
        return new SecretKeySpec(secretBytes, CryptoUtils.ENCRYPT_AES);
    }
    // hex representation
    public static String byte2hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) { result.append(String.format("%02x", b)); }
        return result.toString();
    }
    public static byte[] hex2byte(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) (
                (Character.digit(hex.charAt(i), 16) << 4) +
                Character.digit(hex.charAt(i+1), 16)
            );
        }
        return data;
    }
}
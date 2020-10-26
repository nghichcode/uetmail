package com.nc.uetmail.mail.utils.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

public class CryptorAesCbc {
    private static final String ENCRYPT_ALGO = "AES/CBC/PKCS5Padding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 16;
    private static final int AES_KEY_BIT = 128;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public static class CryptData {
        public byte[] secretBytes;
        public byte[] ivBytes;
        public byte[] textBytes;

        public CryptData(byte[] secretBytes, byte[] ivBytes, byte[] textBytes) {
            this.secretBytes = secretBytes;
            this.ivBytes = ivBytes;
            this.textBytes = textBytes;
        }

        public String getSecret() {
            return CryptoUtils.byte2hex(this.secretBytes);
        }

        public String getIv() {
            return CryptoUtils.byte2hex(this.ivBytes);
        }

        public String getText() {
            return CryptoUtils.byte2hex(this.textBytes);
        }

        @Override
        public String toString() {
            return "CryptData{" +
                    "secretBytes=" + CryptoUtils.byte2hex(secretBytes) +
                    ", ivBytes=" + CryptoUtils.byte2hex(ivBytes) +
                    ", textBytes=" + CryptoUtils.byte2hex(textBytes) +
                    '}';
        }
    }

    public static String getAESKey() throws NoSuchAlgorithmException {
        return CryptoUtils.byte2hex(CryptoUtils.getAESKey(AES_KEY_BIT).getEncoded());
    }
    public static String getRandomNonce() {
        return CryptoUtils.byte2hex(CryptoUtils.getRandomNonce(AES_KEY_BIT));
    }

    public static CryptData encryptWithKey(String text, String secret) throws Exception {
        SecretKey secretKey = CryptoUtils.bytes2AESKey(CryptoUtils.hex2byte(secret));;
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] encryptedText = cipher.doFinal(text.getBytes(UTF_8));
        return new CryptData(secretKey.getEncoded(), iv, encryptedText);
    }

    public static CryptData encrypt(String text) throws Exception {
        SecretKey secret = CryptoUtils.getAESKey(AES_KEY_BIT);
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] encryptedText = cipher.doFinal(text.getBytes(UTF_8));
        return new CryptData(secret.getEncoded(), iv, encryptedText);
    }

    public static String decrypt(CryptData cryptData) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, CryptoUtils.bytes2AESKey(cryptData.secretBytes),
                new IvParameterSpec(cryptData.ivBytes));
        byte[] plainText = cipher.doFinal(cryptData.textBytes);
        return new String(plainText, UTF_8);
    }

    public static void test(String pText) throws Exception {
        String OUTPUT_FORMAT = "%-30s:%s";
        CryptData enc =  CryptorAesCbc.encrypt(pText);
        System.out.printf((OUTPUT_FORMAT) + "%n", "Input (plain text)", pText);
        System.out.printf((OUTPUT_FORMAT) + "%n", "Key (hex)", CryptoUtils.byte2hex(enc.secretBytes));
        System.out.printf((OUTPUT_FORMAT) + "%n", "IV  (hex)", CryptoUtils.byte2hex(enc.ivBytes));
        System.out.printf((OUTPUT_FORMAT) + "%n", "Encrypted (hex) ", CryptoUtils.byte2hex(enc.textBytes));
        System.out.printf((OUTPUT_FORMAT) + "%n", "Decrypted (plain text)", CryptorAesCbc.decrypt(enc));
    }

}
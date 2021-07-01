package com.secure.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SafeSpaceUtils {
    public static final int TYPE_SMS = 1; //type tin nhan de callback
    public static final int TYPE_FILE = 2; //type file de callback
    public static final String ACTION_SEE= "com.android.safespace.see";
    public static final String ACTION_ADD= "com.android.safespace.add";
    public static String ACTION_CREATE_PASS= "com.android.safespae.action_create_pass";
    public static String ACTION_LOGIN= "com.android.safespace_login";
    public static String ACTION_START_SAFE_SPACE_ACTIVITY= "com.android.start_safe_space_activity";
    public static final String ENCRYPT = "encrypt_sms";
    private static final String salt= "salt_safe_space_bphone";


    public static SecretKey getKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        (new SecureRandom()).nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}

package com.application.projecttbh;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static String keyStr = "ssshhhhhhhhhhh!!!!";


    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String strToEncrypt)
    {
        String secret = AES.keyStr;
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    private static String intArrayToString(int[] arr) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < arr.length; i++) {
            builder.append((char)(arr[i]));
        }
        return builder.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt)
    {
        String secret = AES.keyStr;
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

//            byte[] toDecrypt = new byte[strToDecrypt.length()];
//            for(int i = 0; i < strToDecrypt.length(); i++) {
//                char c = strToDecrypt.charAt(i);
//                if(c >= 128) {
//                    c -= 127;
//                    c *= -1;
//                }
//                toDecrypt[i] = (byte)c;
//            }

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}

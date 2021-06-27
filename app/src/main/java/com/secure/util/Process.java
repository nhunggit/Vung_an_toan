package com.secure.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Process {
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static void encrypt(FileInputStream fis, FileOutputStream fos) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decrypt(FileInputStream fis, FileOutputStream fos) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

    private static String password="nhung123";

    public static SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(password.getBytes(), "AES");
    }

    public static byte[] encryptMsg(String message)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        /* Encrypt the message. */
//        Cipher cipher = null;
//        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, secret);
//        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));

        byte[] plaintext = message.getBytes();
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] ciphertext = cipher.doFinal(plaintext);
        byte[] iv = cipher.getIV();
        return iv;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

    public byte[] createPath(String path) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException {
        File file= new File(path);
        String name= file.getName();
        String parent= file.getParent();
//        Log.d("nhungltk", "createPath: "+name+" parent "+parent);
//            String newName= String.valueOf(encryptMsg(name,generateKey()));
//            String newPath= parent + "/" + newName;
//            Log.d("nhungltk", "createPath new: "+newPath);
        return encryptMsg(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void decryptFolder(String pathSource, String pathDestiation) throws FileNotFoundException {
        try {
            File src = new File(pathSource);
            //Bkav Nhungltk: OTA -start
            //sua lai do code truoc bi loi tao 2 thu muc con ben trong thu muc destination
            File dst = new File(pathDestiation);
            //Bkav Nhungltk: OTA-end

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    //Bkav Nhungltk: OTA-start
                    String src1 = (new File(src, files[i])).getPath();
                    String dst1 = (new File(dst,files[i])).getPath();
                    //Bkav Nhungltk: OTA -end
                    decryptFolder(src1, dst1);
                }
            } else {
                decrypt(new FileInputStream(src), new FileOutputStream(dst));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.getParentFile().mkdirs();
        }

        String sourcePath =sourceFile.getPath();
        InputStream source = new FileInputStream(sourcePath);
        String destinationPath =  destFile.getPath();
        OutputStream destination = new FileOutputStream(destinationPath);
        try {
            FileUtils.copy(source, destination);
            //Bkav Nhungltk: OTA -start
            //xoa file da copy thanh cong
            sourceFile.delete();
            //Bkav Nhungltk: OTA -end
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Bkav Nhungltk: OTA -start
    // xoa thu muc giai ma
    public void deleteDirectory(File delete) {
        if (delete.exists()) {
            if (delete.isDirectory()) {
                File[] files = delete.listFiles();
                if(files!=null) {
                    Log.d("nhungltk", "deleteDirectory: "+files);
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            Log.d("nhungltk", "deleteDirectory: file i "+files);
                            deleteDirectory(files[i]);
                        } else {
                            files[i].delete();
                            Log.d("nhungltk", "deleteDirectory: return "+files);
                        }
                    }
                }else{
                    Log.d("nhungltk", "deleteDirectory: delete ");
                    delete.delete();
                }
            }
            delete.delete();
        }
    }
}

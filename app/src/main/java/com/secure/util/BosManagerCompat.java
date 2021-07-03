package com.secure.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Bkav DucLQ lop nay de invoke vao cac lop cua BosManager
 */
public class BosManagerCompat {
    Context context;
    private static final String SAFE_PASS= "safe_pass";
    private static final Object mLock = new Object();
    private final File mInfoSafeSpaceFile;
    private static final String XML_TAG_INFO_SAFE_SPACE = "info_safe_space";//Bkav Nhungltk

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public BosManagerCompat(Context context) throws IOException {
        this.context = context;

        File systemDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + ".Secure");

        //Bkav Nhungltk: tao file de ghi thong tin SafeSpace -start
        File infoSafeDir=new File(systemDir,"info_safe");
        mInfoSafeSpaceFile= new File(infoSafeDir,"info_safe.txt");
        //Bkav Nhungltk: tao file de ghi thong tin SafeSpace -end

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String hashing(String value) {
        //TODO: trong ham nay thuc hien viec bam roi ra ve text
        byte[] salt = new byte[16];
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(salt);
        byte[] hashedPassword = md.digest(value.getBytes(StandardCharsets.UTF_8));
        return new String(hashedPassword);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean saveKeySafe(String key) throws RemoteException {
        try {
            String keyHashed = hashing(key);
            writeInfoSafeFile( keyHashed);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean checkKeySafe(String key) throws RemoteException {
        String passSave = readInfoSafeFile().trim();
        String passHashed = hashing(key).trim();
        Log.d("nhungltk", "checkKeySafe: "+passSave.length()+ "    "+passHashed.length());
        Log.d("nhungltk", "checkKeySafe: "+passHashed.equals(passSave));
        return (passHashed != null && passHashed.equals(passSave));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean hasKey()throws RemoteException {
        return !TextUtils.isEmpty(readInfoSafeFile());
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean writeInfoSafeFile(String value) throws IOException {
        FileWriter fileWriter= new FileWriter(mInfoSafeSpaceFile);
        fileWriter.append(value);
        fileWriter.flush();
        fileWriter.close();
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private String readInfoSafeFile() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(mInfoSafeSpaceFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        Log.d("nhungltk", "readInfoSafeFile: "+text.toString());
        return text.toString();
    }


//    public static final String BOS_SERVICE = "bos";
//    Class<?> mClass;
//    {
//        try {
//            mClass = Class.forName("android.os.bos.BosManager");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public boolean saveKeySafe(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Object mBosManager =context.getSystemService(BOS_SERVICE);
//        if(mBosManager != null && mClass != null){
//            Method method=null;
//            method=mClass.getMethod("saveKeySafe",String.class);
//            method.setAccessible(true);
//            return (boolean) method.invoke(mBosManager,key);
//        }
//        return false;
//    }
//
//    public boolean checkKeySafe(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Object mBosManager =context.getSystemService(BOS_SERVICE);
//        if(mBosManager!= null && mClass != null){
//            Method method= null;
//            method= mClass.getMethod("checkKeySafe", String.class);
//            method.setAccessible(true);
//            return (boolean) method.invoke(mBosManager, key);
//        }
//        return false;
//    }
//
//    public boolean hasKey() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
//        Object mBosManager =context.getSystemService(BOS_SERVICE);
//        if(mBosManager!= null && mClass != null){
//            Method method= null;
//            method= mClass.getMethod("hasKey");
//            method.setAccessible(true);
//            return (boolean) method.invoke(mBosManager);
//        }
//        return false;
//    }


}

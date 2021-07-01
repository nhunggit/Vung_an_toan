package com.secure.util;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Bkav DucLQ lop nay de invoke vao cac lop cua BosManager
 */
public class BosManagerCompat {
    Context context;

    public BosManagerCompat(Context context) {
        this.context = context;
    }

    public static final String BOS_SERVICE = "bos";
    Class<?> mClass;
    {
        try {
            mClass = Class.forName("android.os.bos.BosManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public boolean saveKeySafe(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object mBosManager =context.getSystemService(BOS_SERVICE);
        if(mBosManager != null && mClass != null){
            Method method=null;
            method=mClass.getMethod("saveKeySafe",String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(mBosManager,key);
        }
        return false;
    }

    public boolean checkKeySafe(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object mBosManager =context.getSystemService(BOS_SERVICE);
        if(mBosManager!= null && mClass != null){
            Method method= null;
            method= mClass.getMethod("checkKeySafe", String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(mBosManager, key);
        }
        return false;
    }

    public boolean hasKey() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object mBosManager =context.getSystemService(BOS_SERVICE);
        if(mBosManager!= null && mClass != null){
            Method method= null;
            method= mClass.getMethod("hasKey");
            method.setAccessible(true);
            return (boolean) method.invoke(mBosManager);
        }
        return false;
    }


}

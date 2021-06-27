package com.secure.util;

import android.os.Environment;

import java.io.File;

public class UtilSecure {
    public static final String folderSecure= Environment.getExternalStorageDirectory() +
            File.separator + ".Secure";
    public static final String folderEncrypt= folderSecure + File.separator+".Encrypt";
    public static final String folderDecrypt= folderSecure + File.separator+".Decrypt";
}

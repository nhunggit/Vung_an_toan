package com.secure.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.secure.safespace.Database;
import com.secure.safespace.R;
import com.secure.safespace.SecureActivity;
import com.secure.util.Path;
import com.secure.util.SecureAdapter;
import com.secure.util.UtilSecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import com.secure.util.Process;

public class EncryptAsyncTask extends AsyncTask<Void, Void, Void> {
    private String path;
    private Activity contextParent;
    private GridView gridView;
    private LinearLayout progressBar;
    private Database database;
    private List<Path> list;
    private String password;

    public EncryptAsyncTask(String path, String password){
        this.path= path;
        this.database= database;
        this.list= list;
        this.password= password;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progressBar= (LinearLayout)contextParent.findViewById(R.id.load_activity_process);
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        //progressBar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected Void doInBackground(Void... voids) {
        File origal= new File(path);
        String name =origal.getName();
        String encrypt= UtilSecure.folderEncrypt + File.separator + name;
        String decrypt= UtilSecure.folderDecrypt + File.separator + name;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream= null;
        try {
            fileInputStream = new FileInputStream(path);
            fileOutputStream = new FileOutputStream(encrypt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Process process= new Process(password);
        try {
            process.copyFile(new File(path),new File(decrypt));
            process.encrypt(fileInputStream, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}

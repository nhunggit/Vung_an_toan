package com.secure.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.secure.safespace.R;
import com.secure.util.Path;
import com.secure.util.SecureAdapter;
import com.secure.util.UtilSecure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.secure.util.Process;

public class SecureAsyncTask extends AsyncTask<Void, Void, File> {
    private SecureAdapter secureAdapter;
    private List<Path> list;
    Activity contextParent;
    private File encryptFolder;
    private File decryptFolder;
    private LinearLayout progressBar;
    private GridView gridView;
    String password;

    public SecureAsyncTask(SecureAdapter secureAdapter, List<Path> list, Activity contextParent, String password){
        this.list=list;
        this.secureAdapter= secureAdapter;
        this.contextParent= contextParent;
        this.password= password;
    }

    @Override
    public File doInBackground(Void... voids) {
        File folder = new File(UtilSecure.folderSecure);
        boolean success = true;
        if (!folder.exists()) {
            success=folder.mkdirs();
        }
        if(success){
            encryptFolder = new File(UtilSecure.folderEncrypt);
            if(!encryptFolder.exists()){
                encryptFolder.mkdirs();
            }
            decryptFolder= new File(UtilSecure.folderDecrypt);
            if(!decryptFolder.exists()){
                decryptFolder.mkdirs();
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Process process= new Process(password);
                process.decryptFolder(UtilSecure.folderEncrypt,UtilSecure.folderDecrypt);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return decryptFolder;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        gridView= (GridView)contextParent.findViewById(R.id.recycle_view);
        gridView.setAdapter(secureAdapter);
    }


    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar= (LinearLayout)contextParent.findViewById(R.id.load_activity_process);
        progressBar.setVisibility(View.VISIBLE);
    }
}

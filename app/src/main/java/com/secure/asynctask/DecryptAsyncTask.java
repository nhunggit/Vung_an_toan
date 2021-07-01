package com.secure.asynctask;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.secure.safespace.Database;
import com.secure.safespace.R;
import com.secure.util.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import com.secure.util.Process;

public class DecryptAsyncTask extends AsyncTask<Void, Void, Void> {

    private Activity contextParent;
    private Path path_delete;
    private List<Path> list;
    private LinearLayout progressBar;
    private String password;

    public DecryptAsyncTask(Activity contextParent, Path path_delete, List<Path> list, String password){
        this.contextParent= contextParent;
        this.path_delete= path_delete;
        this.list= list;
        this.password= password;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Process process= new Process(password);
            process.decrypt(new FileInputStream(path_delete.getPathEncrypt()),new FileOutputStream(path_delete.getPathDecrypt()));
            MediaScannerConnection.scanFile(contextParent, new String[]{path_delete.getPathDecrypt()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("nhungltk", "onScanCompleted: ");
                        }
                    });
            list.remove(path_delete);
            Database.getInstance(contextParent).deletePath(path_delete.getPathEncrypt(),path_delete.getPathDecrypt());
            File file= new File(path_delete.getPathEncrypt());
            file.delete();
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar= (LinearLayout)contextParent.findViewById(R.id.load_activity_process);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

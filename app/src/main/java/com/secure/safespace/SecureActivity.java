package com.secure.safespace;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.secure.asynctask.DecryptAsyncTask;
import com.secure.asynctask.EncryptAsyncTask;
import com.secure.asynctask.SecureAsyncTask;
import com.secure.util.Path;
import com.secure.util.SafeSpaceUtils;
import com.secure.util.SecureAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.secure.util.Process;
import com.secure.util.UtilSecure;

import net.sqlcipher.database.SQLiteDatabase;

import javax.crypto.NoSuchPaddingException;

public class SecureActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private Button choosseFile;
    private ImageButton mArowBack;
    private Button menu;
    private static final int FILE_SELECT_CODE = 0;
    String path = null;
    public List<Path> list;
    SecureAdapter secureAdapter;
    Process process;
    Database database;
    SecureAsyncTask secureAsyncTask;
    private String folderSecure= Environment.getExternalStorageDirectory() +
            File.separator + ".Secure";
    private String folderEncrypt= folderSecure + File.separator+".Encrypt";
    private String folderDecrypt= folderSecure + File.separator+".Decrypt";
    private File encryptFolder;
    private File decryptFolder;
    private  int mPosition;
    private static String password;
    callbackListener callbackListener= new callbackListener () {
        @Override
        public void callback(int posistion) {
            mPosition= posistion;
            FragmentManager manager = getSupportFragmentManager();
            DetailFileFragment fragmentExternalView= new DetailFileFragment(posistion,list, callbackListener);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, fragmentExternalView).addToBackStack(null);
            transaction.commit();
            choosseFile.setVisibility(View.GONE);
            menu.setVisibility(View.VISIBLE);
        }

        @Override
        public void updateMenu(boolean hasMenu) {
            if(!hasMenu) {
                menu.setVisibility(View.GONE);
                choosseFile.setVisibility(View.VISIBLE);
            }else{
                menu.setVisibility(View.VISIBLE);
                choosseFile.setVisibility(View.GONE);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_app);
        Intent intent= getIntent();
        password= intent.getStringExtra("password");
        process = new Process(password);
        path= intent.getStringExtra("path");

        choosseFile= (Button)findViewById(R.id.bt_choose_file);
        choosseFile.setOnClickListener(this);
        mArowBack= (ImageButton)findViewById(R.id.arow_back);
        mArowBack.setOnClickListener(this);
        menu= (Button)findViewById(R.id.menu);
        menu.setOnClickListener(this);

        SQLiteDatabase.loadLibs(this);
        database=Database.getInstance(this);
        list=Database.getInstance(this).getList();

        secureAdapter= new SecureAdapter(list,callbackListener);

        secureAsyncTask= new SecureAsyncTask(secureAdapter, list,
                SecureActivity.this, password);
        secureAsyncTask.execute();

        FragmentManager manager = getSupportFragmentManager();
        AllFileFragment fragment= new AllFileFragment(secureAdapter, callbackListener);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        if(path!=null){
            processEncrypt();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void processEncrypt(){
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
            database.insertPath(encrypt, path, decrypt);
            list.add(0, new Path(encrypt,path,decrypt));
            secureAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==(R.id.bt_choose_file)) {
            showFileChooser();
        }else if(v.getId()==R.id.arow_back){
            finish();
        }else if(v.getId()==R.id.menu){
            PopupMenu popupMenu= new PopupMenu(getApplicationContext(), v);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==(R.id.decrypt)){
                        Path path_delete= list.get(mPosition);
                        DecryptAsyncTask decryptAsyncTask= new DecryptAsyncTask(SecureActivity.this, path_delete, list, password);
                        decryptAsyncTask.execute();
                        getSupportFragmentManager().popBackStack();
                    }
                    return false;
                }
            });
            popupMenu.inflate(R.menu.menu);
            popupMenu.show();
        }

    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("nhungltk", "onActivityResult: ");
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    try {
                        path = process.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (path != null) {
                        Intent intent= new Intent(SecureActivity.this, PasswordActivity.class);
                        intent.setAction(SafeSpaceUtils.ACTION_LOGIN);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        decryptFolder= secureAsyncTask.doInBackground();
        process.deleteDirectory(decryptFolder);
    }

    public interface callbackListener{
        void callback(int posistion);
        void updateMenu(boolean hasMenu);
    }
}

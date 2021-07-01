package com.secure.safespace;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;

import com.secure.asynctask.EncryptAsyncTask;
import com.secure.util.BosManagerCompat;
import com.secure.util.SafeSpaceUtils;
/**
 * Bkav DucLQ giao dien nay de nhap password cho cac kich ban khac nhau
 */
public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mLayoutCreatePass;
    private LinearLayout mLayoutLogin;
    private EditText mPassCreate;
    private EditText mConfirmPassWord;
    private EditText mPassLogin;
    private Button mButtonCreatePass;
    private Button mButtonlogin;
    private BosManagerCompat mBosManagerCompat;
    private Context mContext;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        mContext = getApplicationContext();
        mBosManagerCompat = new BosManagerCompat(mContext);
        mLayoutCreatePass = (LinearLayout) findViewById(R.id.ll_create_pass);
        mLayoutLogin = (LinearLayout) findViewById(R.id.ll_login);
        mPassCreate = (EditText) findViewById(R.id.enter_pass);
        mConfirmPassWord = (EditText) findViewById(R.id.confirm_pass);
        mPassLogin = (EditText) findViewById(R.id.pass_word);
        mButtonCreatePass = (Button) findViewById(R.id.create_password);
        mButtonlogin = (Button) findViewById(R.id.login);
        mButtonCreatePass.setOnClickListener(this);
        mButtonlogin.setOnClickListener(this);
        Intent intent = getIntent();
        path=intent.getStringExtra("path");
        if (intent.getAction().equals(SafeSpaceUtils.ACTION_CREATE_PASS))
            initCreatePassWord();
        else if (intent.getAction().equals(SafeSpaceUtils.ACTION_LOGIN))
            initLogin();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.create_password) {
            String password = String.valueOf(mPassCreate.getText());
            String passConfirm = String.valueOf(mConfirmPassWord.getText());
            if (password != null && passConfirm != null && password.equals(passConfirm)) {
                try {
                    if (mBosManagerCompat.saveKeySafe(password)) {
                        Toast.makeText(mContext, "Create Password success", Toast.LENGTH_SHORT).show();
                        initLogin();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else
                Toast.makeText(mContext, "Password confirm was wrong", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.login) {
            String password = String.valueOf(mPassLogin.getText());
            if (password != null) {
                try {
                    if (mBosManagerCompat.checkKeySafe(password)) {
//                        ActivityCompat.requestPermissions(PasswordActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
//                        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
                        //if(getIntent().getBooleanExtra("encrypt", false)==true) {
//                        Intent intent = new Intent();
//                        intent.putExtra("pass", password);
//                        sendBroadcast(intent);
//                        finish();
                        //setPass.setPass(password);
                        if(path!=null){
                            EncryptAsyncTask encryptAsyncTask= new EncryptAsyncTask(path, password);
                            encryptAsyncTask.execute();
                            finish();
                        }else {
                            Intent intent = new Intent(PasswordActivity.this, SecureActivity.class);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "PassWork was not null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initCreatePassWord() {
        mLayoutCreatePass.setVisibility(View.VISIBLE);
        mLayoutLogin.setVisibility(View.GONE);
    }

    public void initLogin() {
        mLayoutLogin.setVisibility(View.VISIBLE);
        mLayoutCreatePass.setVisibility(View.GONE);
    }


}

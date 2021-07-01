package com.secure.safespace;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.biometric.BiometricPrompt;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.secure.util.BosManagerCompat;
import com.secure.util.SafeSpaceUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static String[] REQUESTED_PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    private Button login;
    private TextView logError;
    private BosManagerCompat bosManagerCompat;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        login= (Button)findViewById(R.id.login);
        login.setOnClickListener(this);

        logError=(TextView)findViewById(R.id.log_error);

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();
//                Intent intent= new Intent(getApplicationContext(),SecureActivity.class);
//                startActivity(intent);
                context= getApplicationContext();
                bosManagerCompat= new BosManagerCompat(context);
                try {
                    if(!bosManagerCompat.hasKey()){
                        Intent intent= new Intent(MainActivity.this,PasswordActivity.class);
                        intent.setAction(SafeSpaceUtils.ACTION_CREATE_PASS);
                        startActivity(intent);
                       // finish();
                    }else{
                        Intent intent= new Intent(MainActivity.this,PasswordActivity.class);
                        intent.setAction(SafeSpaceUtils.ACTION_LOGIN);
                        startActivity(intent);
                        //finish();
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            }

            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                generateSecretKey(new KeyGenParameterSpec.Builder("keyname", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build());
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login for my app").setDescription("Login in using your biometric credential").setNegativeButtonText("Use account password").build();

        SQLiteDatabase.loadLibs(this);

    }
    private static void copyBooleanExtra(Intent from, Intent to, String name,
                                         boolean defaultValue) {
        to.putExtra(name, from.getBooleanExtra(name, defaultValue));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    private SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return ((SecretKey) keyStore.getKey("keyname", null));
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    //int REQUEST_CODE;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), String.valueOf(REQUESTED_PERMISSION)) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    String.valueOf(REQUESTED_PERMISSION))) {
            } else {
                ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        FingerprintManager fingerprintManager= (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        KeyguardManager keyguardManager= (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){
            logError.setText("Fingerprint Scanner not detected in Device");
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED){
            logError.setText("Permission not granted to use Fingerprint Scanner");
        }else if(!keyguardManager.isKeyguardSecure()){
            logError.setText("Add Lock to your phone in Settings");
        }else if(!fingerprintManager.hasEnrolledFingerprints()){
            logError.setText("You should add atleast 1 Fingerprint to use this Feature");
        }else {
            if (v.getId() == R.id.login) {
                try {
                    authenticationKey(getCipher().ENCRYPT_MODE);
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void authenticationKey(int mode){
        Cipher cipher = null;
        try {
            cipher = getCipher();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secretKey = null;
        try {
            secretKey = getSecretKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(mode,secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
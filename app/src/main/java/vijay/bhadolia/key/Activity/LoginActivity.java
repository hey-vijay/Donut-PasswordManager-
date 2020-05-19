package vijay.bhadolia.key.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.Executor;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.Utility.ConstKeyWord;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout etPassword;
    private Button btEnter;
    private TextView tvUseSensor;
    private boolean prefFingerPrint = false;
    private boolean prefFakePassword = false;
    private String passwordKEY;

    //FingerPrint setUp
    androidx.biometric.BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPassword = findViewById(R.id.et_layout_1);
        btEnter = findViewById(R.id.login_bt_enter);
        tvUseSensor = findViewById(R.id.login_tv_use_sensor);
        etPassword.requestFocus();
        loadData();

        Executor executor;
        if(prefFingerPrint){
            tvUseSensor.setVisibility(View.VISIBLE);
            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt (LoginActivity.this, executor,
                    new BiometricPrompt.AuthenticationCallback(){
                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            Toast.makeText(getBaseContext(), "not recognised", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            i.putExtra(ConstKeyWord.isUnlock, true);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock")
                    .setNegativeButtonText("Use Password instead")
                    .build();
            //biometricPrompt.authenticate(promptInfo);
       }

        tvUseSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        //Enter button will throw to the homeScreen
        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPassword.getEditText().getText().length() > 3 && prefFakePassword){
                    Intent i = new Intent(v.getContext(), MainActivity.class);
                    i.putExtra(ConstKeyWord.isUnlock, false);
                    startActivity(i);
                    finish();
                }else{
                    etPassword.setError("Incorrect Password");
                }
            }
        });

        etPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordKEY.equals(s.toString())){
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra(ConstKeyWord.isUnlock, true);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConstKeyWord.KEY, MODE_PRIVATE);
        passwordKEY = sharedPreferences.getString(ConstKeyWord.KEY, "");
        prefFingerPrint = sharedPreferences.getBoolean(ConstKeyWord.FINGERPRINT, false);
        prefFakePassword = sharedPreferences.getBoolean(ConstKeyWord.FAKEPASSWORD, false);
    }

}

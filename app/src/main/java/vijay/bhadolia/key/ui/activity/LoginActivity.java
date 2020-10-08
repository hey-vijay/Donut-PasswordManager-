package vijay.bhadolia.key.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.util.SmartPreferences;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private TextInputLayout etPassword;
    private Button btEnter;
    private ImageView tvOpenBioMetricSensor;
    private boolean isFingerPrintUnable = false;
    private boolean isFakeEntryUnable = false;
    private String masterPassword;

    //FingerPrint setUp
    androidx.biometric.BiometricPrompt biometricPrompt;
    Executor executor;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
        loadData();

        if (isFingerPrintUnable) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                tvOpenBioMetricSensor.setVisibility(View.VISIBLE);
                setUpBioMetricPrompt();
            }
        }

        tvOpenBioMetricSensor.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                biometricPrompt.authenticate(promptInfo);
            }
        });

        btEnter.setOnClickListener(v -> {
            String enteredText = etPassword.getEditText().getText().toString();
            if (enteredText.length() > 4 && isFakeEntryUnable) {
                openMainActivity(false);
            } else {
                if(enteredText.equals(masterPassword)){
                    openMainActivity(true);
                } else {
                    etPassword.setError(getString(R.string.incorrect_password));
                }
            }
        });

        if(etPassword.getEditText() != null) {
            Log.d(TAG, "onCreate: textWatcher attached");
            etPassword.getEditText().addTextChangedListener(textWatcher);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (masterPassword.equals(s.toString())) {
                openMainActivity(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Do nothing
        }
    };

    private void setUpBioMetricPrompt() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NotNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Log.d(TAG, "onAuthenticationError: " + errString.toString());
                        Toast.makeText(LoginActivity.this, getString(R.string.not_recognise), Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getBaseContext(), getString(R.string.authentication_successful), Toast.LENGTH_SHORT)
                                .show();
                        openMainActivity(true);

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setNegativeButtonText(getString(R.string.biometric_negative_btton))
                .build();
        //biometricPrompt.authenticate(promptInfo);
    }

    private void openMainActivity(boolean correctPasswordEntered) {
        SmartPreferences.getInstance(this).saveValue(Constants.SHOW_LAST_ACTIVE_STATUS, true);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(Constants.IS_UNLOCK, correctPasswordEntered);
        startActivity(i);
        finish();
    }

    private void initViews() {
        etPassword = findViewById(R.id.et_layout_1);
        btEnter = findViewById(R.id.login_bt_enter);
        tvOpenBioMetricSensor = findViewById(R.id.login_tv_use_sensor);
        etPassword.requestFocus();
    }

    private void loadData() {
        //SmartPreferences.getInstance(this).saveValue(Constants.KEY, "860290");
        masterPassword = SmartPreferences.getInstance(this).getValue(Constants.MASTER_PASSWORD, "");
        isFingerPrintUnable = SmartPreferences.getInstance(this).getValue(Constants.FINGERPRINT_ENABLE, false);
        isFakeEntryUnable = SmartPreferences.getInstance(this).getValue(Constants.FAKE_PASSWORD_ENABLE, false);

        Log.d(TAG, "loadData: " + masterPassword);
    }

}

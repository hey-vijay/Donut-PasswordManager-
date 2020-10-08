package vijay.bhadolia.key.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.util.SmartPreferences;

public class SignUpActivity extends AppCompatActivity {

    private String KEY = "";
    private boolean isFirstTime = false;
    private TextInputLayout etMasterPassword;
    private TextInputLayout etMasterPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadData();

        if(!isFirstTime) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        initView();
        etMasterPassword = findViewById(R.id.sign_up_et_master_password);
        etMasterPassword2 = findViewById(R.id.sign_up_et_master_password2);
        Button btRegister = findViewById(R.id.sign_up_bt_register);
        btRegister.setOnClickListener(v -> setMasterPassword());
    }

    private void initView() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setMasterPassword(){
        String text1, text2;
        text1 = etMasterPassword.getEditText().getText().toString();
        text2 = etMasterPassword2.getEditText().getText().toString();

        if(text1.equals(text2)){
            if(text1.length() < 5){
                etMasterPassword.setError("Password is too short");
            }else{
                KEY = text1;
                SmartPreferences.getInstance(this).saveValue(Constants.MASTER_PASSWORD, KEY);
                SmartPreferences.getInstance(this).saveValue(Constants.FIRST_TIME, false);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constants.IS_UNLOCK, true);
                startActivity(intent);
                finish();
            }
        }else {
            etMasterPassword.setError("Password do not match");
            etMasterPassword2.setError("Password do not match");
        }
    }

    private void loadData(){
        KEY =  SmartPreferences.getInstance(this).getValue(Constants.MASTER_PASSWORD, "");
        isFirstTime = SmartPreferences.getInstance(this).getValue(Constants.FIRST_TIME, true);
    }
}

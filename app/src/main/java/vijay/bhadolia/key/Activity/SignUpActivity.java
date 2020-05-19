package vijay.bhadolia.key.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.Utility.ConstKeyWord;

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

        if(isFirstTime){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        etMasterPassword = findViewById(R.id.sign_up_et_master_password);
        etMasterPassword2 = findViewById(R.id.sign_up_et_master_password2);
        Button btRegister = findViewById(R.id.sign_up_bt_register);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMasterPassword();
            }
        });
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
                saveData();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("isUnlock", true);
                startActivity(intent);
                finish();
            }
        }else {
            etMasterPassword.setError("Incorrect Password.");
            etMasterPassword2.setError("Incorrect Password.");
        }
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConstKeyWord.KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstKeyWord.KEY, KEY);
        editor.putBoolean(ConstKeyWord.FIRSTTIME, true);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConstKeyWord.KEY, MODE_PRIVATE);
        KEY = sharedPreferences.getString(ConstKeyWord.KEY,"");
        isFirstTime = sharedPreferences.getBoolean(ConstKeyWord.FIRSTTIME, false);
    }
}

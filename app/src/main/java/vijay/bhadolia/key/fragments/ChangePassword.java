package vijay.bhadolia.key.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vijay.bhadolia.key.R;

public class ChangePassword extends Fragment {

    public ChangePassword() {
        // Required empty public constructor
    }
    private String currentPassword;
    private String newPassword1;
    private String newPassword2;

    private TextInputLayout etCurrentPassword;
    private TextInputLayout etNewPassword1;
    private TextInputLayout etNewPassword2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        etCurrentPassword = view.findViewById(R.id.chng_pswd_et_current_password);
        etNewPassword1 = view.findViewById(R.id.chng_pswd_et_password1);
        etNewPassword2 = view.findViewById(R.id.chng_pswd_et_password2);
        Button btUpdate = view.findViewById(R.id.chng_pswd_bt_update);
        loadData();
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyPassword()){
                    updatePassword();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(
                            R.id.main_container,
                            new DashboardFragment())
                            .commit();
                }
            }
        });

        return view;
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("KEY", Context.MODE_PRIVATE);
        currentPassword = sharedPreferences.getString("KEY", "");
    }

    private boolean verifyPassword(){
        String currentText = etCurrentPassword.getEditText().getText().toString();
        newPassword1 = etNewPassword1.getEditText().getText().toString();
        newPassword2 = etNewPassword2.getEditText().getText().toString();

        if(!currentPassword.equals(currentText)){
            //Toast.makeText(getActivity(), "Equal", Toast.LENGTH_SHORT).show();
            etCurrentPassword.setError("Incorrect password");
            return false;
        }

        if(newPassword1.length() < 4 || newPassword2.length() < 4){
            etNewPassword1.setError("password is too short");
            etNewPassword2.setError("password is too short");
            return false;
        }

        if(!newPassword1.equals(newPassword2)){
            etNewPassword1.setError("password do not match");
            etNewPassword2.setError("password do not match");
            return false;
        }
        return true;
    }

    private void updatePassword(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("KEY", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("KEY", newPassword1).apply();
        Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();

    }

}

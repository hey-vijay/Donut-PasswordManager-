package vijay.bhadolia.key.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.util.SmartPreferences;

public class ChangePassword extends Fragment {

    public ChangePassword() {
        // Required empty public constructor
    }

    private String oldPassword;

    private TextInputLayout etCurrentPassword;
    private TextInputLayout etNewPassword1;
    private TextInputLayout etNewPassword2;
    private Button updatePassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Change Password");

        etCurrentPassword = view.findViewById(R.id.til_enterOldPassword);
        etNewPassword1 = view.findViewById(R.id.til_enterNewPassword);
        etNewPassword2 = view.findViewById(R.id.til_reEnterNewPassword);
        updatePassword = view.findViewById(R.id.btUpdatePassword);

        getData();

        etCurrentPassword.getEditText().addTextChangedListener(textWatcher);
        etNewPassword1.getEditText().addTextChangedListener(textWatcher);
        etNewPassword2.getEditText().addTextChangedListener(textWatcher);


        //Change Password
        view.findViewById(R.id.btUpdatePassword).setOnClickListener((view1 -> {
            if (verifyPassword()) {
                updatePassword(etNewPassword1.getEditText().getText().toString());
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(
                        R.id.main_container,
                        new DashboardFragment())
                        .commit();
            }
        }));
    }

    private void getData() {
        oldPassword = SmartPreferences.getInstance(getContext()).getValue(Constants.MASTER_PASSWORD, "");
    }

    private boolean verifyPassword() {
        String currentText = etCurrentPassword.getEditText().getText().toString();
        String newPassword1 = etNewPassword1.getEditText().getText().toString();
        String newPassword2 = etNewPassword2.getEditText().getText().toString();

        if (newPassword1.length() < 4 || newPassword2.length() < 4) {
            etNewPassword1.setError("password is too short");
            etNewPassword2.setError("password is too short");
            return false;
        }

        if (!newPassword1.equals(newPassword2)) {
            etNewPassword1.setError("password do not match");
            etNewPassword2.setError("password do not match");
            return false;
        }

        if (!oldPassword.equals(currentText)) {
            etCurrentPassword.setError("Incorrect password");
            return false;
        }
        return true;
    }

    private void updatePassword(String password) {
        SmartPreferences.getInstance(getContext()).saveValue(Constants.MASTER_PASSWORD, password);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //Do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeBackground(isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Do nothing
        }
    };

    private void changeBackground(boolean isEmpty) {
        if (isEmpty) {
            updatePassword.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_button_gradient));
        } else {
            updatePassword.setBackground(ContextCompat.getDrawable(getContext(), R.color.grey_300));
        }
    }

    private boolean isEmpty() {
        boolean et_1 = etCurrentPassword.getEditText().getText().toString().trim().isEmpty();
        boolean et_2 = etNewPassword1.getEditText().getText().toString().trim().isEmpty();
        boolean et_3 = etNewPassword2.getEditText().getText().toString().trim().isEmpty();
        return (!et_1 && !et_2 && !et_3);
    }
}

package vijay.bhadolia.key.fragments;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.Utility.ConstKeyWord;

/*
*   Not stable yet.
*   TODO: check for all different api versions.
*    check if the device has biometric sensor or not
*   TODO: encrypt and Backup all the password
* */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    private Switch aSwitchFingerPrint;
    private Switch aSwitchAllowWrongPassword;
    private Button btChangePassword;
    private Button btdeleteAllPassword;
    private TextView tvFakePassword;
    private LinearLayout llFakePassword;
    private boolean fingerprintIsEnable;
    private boolean fakePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        loadData();

        btChangePassword.setOnClickListener(this);
        tvFakePassword.setOnClickListener(this);
        btdeleteAllPassword.setOnClickListener(this);

        aSwitchFingerPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //turn on fingerPrint unlock functionality
                    Toast.makeText(getActivity(), "ON", Toast.LENGTH_SHORT).show();
                    if (isHardwareSupported(getActivity()) && isFingerprintAvailable(getActivity())) {
                        setFingerPrintUnlock(true);
                    }else {
                        Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
                        aSwitchFingerPrint.setChecked(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "off", Toast.LENGTH_SHORT).show();
                    setFingerPrintUnlock(false);
                }
            }
        });

        aSwitchAllowWrongPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstKeyWord.KEY, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(ConstKeyWord.FAKEPASSWORD, true).apply();
                    Toast.makeText(getActivity(), "on", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstKeyWord.KEY, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(ConstKeyWord.FAKEPASSWORD, false).apply();
                    Toast.makeText(getActivity(), "off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void init(View view){
        aSwitchFingerPrint = view.findViewById(R.id.setting_switch_fingerprint);
        aSwitchAllowWrongPassword = view.findViewById(R.id.setting_switch_allow_fake_password);
        btChangePassword = view.findViewById(R.id.setting_bt_change_password);
        btdeleteAllPassword = view.findViewById(R.id.setting_bt_delete_all_password);
        tvFakePassword = view.findViewById(R.id.setting_tv_fake_password);
        llFakePassword = view.findViewById(R.id.setting_ll_fake_password);
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstKeyWord.KEY, Context.MODE_PRIVATE);
         fingerprintIsEnable =  sharedPreferences.getBoolean(ConstKeyWord.FINGERPRINT, false);
         fakePassword = sharedPreferences.getBoolean(ConstKeyWord.FAKEPASSWORD, false);
        if(fingerprintIsEnable){
            aSwitchFingerPrint.setChecked(true);
        }
        if(fakePassword){
            aSwitchAllowWrongPassword.setChecked(true);
        }
    }

    private void setFingerPrintUnlock(boolean enable){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("KEY", Context.MODE_PRIVATE);
        if(enable){
            sharedPreferences.edit().putBoolean(ConstKeyWord.FINGERPRINT, true).apply();
        }else{
            sharedPreferences.edit().putBoolean(ConstKeyWord.FINGERPRINT, false).apply();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setting_bt_change_password: {
                getActivity().getSupportFragmentManager().beginTransaction().addSharedElement(btChangePassword, ViewCompat.getTransitionName(btChangePassword)).replace(
                        R.id.main_container,
                        new ChangePassword()).commit();
                break;
            }
            case R.id.setting_tv_fake_password:{
                if(llFakePassword.getVisibility() == View.VISIBLE){
                    llFakePassword.setVisibility(View.GONE);
                    tvFakePassword.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(
                                    getActivity(),R.drawable.ic_right_arrow),
                            null);
                }else{
                    llFakePassword.setVisibility(View.VISIBLE);
                    tvFakePassword.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(
                                    getActivity(),R.drawable.ic_bottom_arrow),
                            null);
                }
                break;
            }
            case R.id.setting_bt_delete_all_password:
                Toast.makeText(getActivity(), "Delete all Password", Toast.LENGTH_SHORT).show();
                //TODO: Delete all password action
                break;
        }
    }

    public boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }


    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since fingerprint authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     *
     * */
    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }


    /*
     * Condition II: Check if the device has fingerprint sensors.
     * Note: If you marked android.hardware.fingerprint as something that
     * your app requires (android:required="true"), then you don't need
     * to perform this check.
     *
     * */
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }


    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }


    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }
}

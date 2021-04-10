package vijay.bhadolia.key.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.util.SmartPreferences;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getName();

    public SettingsFragment() {
        // Required empty public constructor
    }

    private SwitchCompat fingerPrintSwitch;
    private SwitchCompat fakeEntrySwitch;
    private SwitchCompat chkClipboardCopyOption, chkDisableBlur;
    private RelativeLayout rlFakeEntry;
    private ImageView imgRightArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        loadData();


        fingerPrintSwitch.setOnCheckedChangeListener(checkedChangeListener);
        fakeEntrySwitch.setOnCheckedChangeListener(checkedChangeListener);
        chkClipboardCopyOption.setOnCheckedChangeListener(checkedChangeListener);
        chkDisableBlur.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void init(View view) {

        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.title_settings));

        fingerPrintSwitch = view.findViewById(R.id.swEnableBiometric);
        fakeEntrySwitch = view.findViewById(R.id.swEnableFakeEntry);
        chkClipboardCopyOption = view.findViewById(R.id.swEnableClipboard);
        chkDisableBlur = view.findViewById(R.id.swDisableBlur);
        rlFakeEntry = view.findViewById(R.id.rlEnableFakeEntry);
        imgRightArrow = view.findViewById(R.id.imgRightArrow);

        view.findViewById(R.id.rlFakeEntry).setOnClickListener(this);
        view.findViewById(R.id.btChangeMasterPassword).setOnClickListener(this);
        view.findViewById(R.id.btDeleteAllPassword).setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = ((compoundButton, state) -> {
        Log.d(TAG, "checked Listener : " + compoundButton.getId() + " state-> " + state);
        String alertText = "";
        switch (compoundButton.getId()) {
            case R.id.swEnableFakeEntry:
                SmartPreferences.getInstance(getContext()).saveValue(Constants.FAKE_PASSWORD_ENABLE, state);
                alertText = "The will also turned on the Fast Enter mode means you don't have to press Enter button.";
                if(state) {
                    showAlert(alertText);
                }
                break;
            case R.id.swEnableBiometric:
                validateAndEnableBiometricSensor(state);
                break;
            case R.id.swDisableBlur:
                alertText = "This is experimental feature!";
                if(state) {
                    showAlert(alertText);
                }
                SmartPreferences.getInstance(getContext()).saveValue(Constants.DISABLE_BLUR_BACKGROUND, state);
                break;
            case R.id.swEnableClipboard:
                if (state) {
                    alertText = getString(R.string.alert_copy_password);
                    showAlert(alertText);
                }
                SmartPreferences.getInstance(getContext()).saveValue(Constants.COPY_ENABLE, state);
                break;
        }
    });

    private void showAlert(String alertText) {
        if (getContext() == null) return;
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.alert))
                .setMessage(alertText)
                .create();
        alertDialog.show();
    }

    private void loadData() {
        boolean isBiometricEnabled = SmartPreferences.getInstance(getContext()).getValue(Constants.FINGERPRINT_ENABLE, false);
        boolean fakePassword = SmartPreferences.getInstance(getContext()).getValue(Constants.FAKE_PASSWORD_ENABLE, false);
        boolean blurDisabled = SmartPreferences.getInstance(getContext()).getValue(Constants.DISABLE_BLUR_BACKGROUND, false);
        boolean copyToClipBoard = SmartPreferences.getInstance(getContext()).getValue(Constants.COPY_ENABLE, false);

        fingerPrintSwitch.setChecked(isBiometricEnabled);
        fakeEntrySwitch.setChecked(fakePassword);
        chkDisableBlur.setChecked(blurDisabled);
        chkClipboardCopyOption.setChecked(copyToClipBoard);
    }

    private void validateAndEnableBiometricSensor(boolean enable) {
        if (enable) {
            if (isHardwareSupported(getActivity()) && isFingerprintAvailable(getActivity())) {
                SmartPreferences.getInstance(getContext()).saveValue(Constants.FINGERPRINT_ENABLE, true);
            } else {
                Toast.makeText(getActivity(), R.string.biometric_sensor_not_available, Toast.LENGTH_SHORT).show();
                fingerPrintSwitch.setChecked(false);
            }
        } else {
            //Turn off BioMetric sensor
            Toast.makeText(getActivity(), getString(R.string.off), Toast.LENGTH_SHORT).show();
            SmartPreferences.getInstance(getContext()).saveValue(Constants.FINGERPRINT_ENABLE, false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btChangeMasterPassword: {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container,
                                new ChangePassword()).commit();
                break;
            }
            case R.id.rlFakeEntry:
                if (rlFakeEntry.getVisibility() == View.VISIBLE) {
                    rlFakeEntry.setVisibility(View.GONE);
                    imgRightArrow.animate()
                            .setDuration(300)
                            .rotation(0)
                            .start();
                } else {
                    rlFakeEntry.setVisibility(View.VISIBLE);
                    imgRightArrow.animate()
                            .setDuration(300)
                            .rotation(90)
                            .start();
                }
                break;
            case R.id.btDeleteAllPassword:
                Toast.makeText(getActivity(), R.string.delete_all_password, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btCreateBackup:
                Toast.makeText(getActivity(), R.string.wip_will_add_soon, Toast.LENGTH_SHORT).show();
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
     * Note: If your project's minSdk version is 23 or higher,
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

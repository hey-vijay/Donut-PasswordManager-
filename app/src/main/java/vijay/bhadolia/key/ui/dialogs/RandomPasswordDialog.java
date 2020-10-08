package vijay.bhadolia.key.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

import vijay.bhadolia.key.Interfaces.AddPasswordListener;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.model.Password;
import vijay.bhadolia.key.util.UtilityFunctions;

public class RandomPasswordDialog extends Dialog {

    private static final String TAG = RandomPasswordDialog.class.getName();

    private Context mContext;
    private AddPasswordListener mListener;
    private int resourceId = -1;
    private String resourceIconName = "";

    TextInputLayout etTitle;
    TextInputLayout etLoginId;
    TextInputLayout etPassword;
    ImageView accountIcon;
    Button btRegeneratePassword, savePassword;
    FragmentManager fragmentManager;

    public RandomPasswordDialog(Context context, FragmentManager childFragmentManager, AddPasswordListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        fragmentManager = childFragmentManager;
        UtilityFunctions.length = 7;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_generate_password);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        etTitle = findViewById(R.id.dialog_et_label);
        etLoginId = findViewById(R.id.dialog_et_account_name);
        etPassword = findViewById(R.id.dialog_et_password);
        accountIcon = findViewById(R.id.accountIcon);
        btRegeneratePassword = findViewById(R.id.dialog_bt_regenerate);
        savePassword = findViewById(R.id.dialog_add_bt_save);

        //attach textWatcher to the edit texts
        etTitle.getEditText().addTextChangedListener(watcher);
        etLoginId.getEditText().addTextChangedListener(watcher);
        etPassword.getEditText().addTextChangedListener(watcher);

        //add onClickListener
        accountIcon.setOnClickListener((view -> openBottomSheet()));

        btRegeneratePassword.setOnClickListener(view -> generateRandomPassword());

        savePassword.setOnClickListener((view -> {
            if(isEmpty()) {
                Password password = new Password(
                        etTitle.getEditText().getText().toString().trim(),
                        etLoginId.getEditText().getText().toString().trim(),
                        etPassword.getEditText().getText().toString().trim(),
                        resourceIconName
                );
                mListener.onSaveButtonClicked(password, true);
                dismiss();
            } else {
                Toast.makeText(mContext, "Field can't be empty", Toast.LENGTH_SHORT)
                        .show();
                Log.d(TAG, "onCreate: empty field");
            }
        }));
        generateRandomPassword();
    }

    private void generateRandomPassword() {
        String text = UtilityFunctions.getRandomText();
        etPassword.getEditText().setText(text);
    }

    private void openBottomSheet() {
        IconBottomSheet iconBottomSheet = new IconBottomSheet(mContext, (iconDrawable, resId) -> {
            accountIcon.setImageDrawable(iconDrawable);
            resourceId = resId;
            resourceIconName = getContext().getResources().getResourceEntryName(resId);
        });
        iconBottomSheet.show(fragmentManager, null);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isEmpty()) {
                savePassword.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_button_gradient));
            } else {
                savePassword.setBackground(ContextCompat.getDrawable(getContext(), R.color.grey_300));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

/*    public AlertDialog.Builder getDialogInstance(Context mContext) {

        UtilityFunctions.length = 7;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //LayoutInflater layoutInflater = mContext;
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_generate_password, null);

        final TextInputLayout finalEtLabel = view.findViewById(R.id.dialog_et_label);
        final TextInputLayout etAccountName = view.findViewById(R.id.dialog_et_account_name);
        final TextInputLayout etPassword = view.findViewById(R.id.dialog_et_password);

        builder.setView(view)
                .setTitle("Password Generator!")
                .setNegativeButton("cancel", (dialog, which) -> {
                    //Do nothing
                })
                .setPositiveButton("save", (dialog, which) -> {
                    String AccName = etAccountName.getEditText().getText().toString();
                    String AccPassword = etPassword.getEditText().getText().toString();
                    String label = finalEtLabel.getEditText().getText().toString();
                    if (AccPassword.isEmpty() || AccName.isEmpty() || label.isEmpty()) {
                        Toast.makeText(mContext, "All field must be initialized", Toast.LENGTH_SHORT).show();
                    } else {
                        //TODO: Update Random Password
                        mListener.onSaveButtonClicked(null, true);
                    }
                });

        etPassword.getEditText().setText(UtilityFunctions.getRandomText());
        Button btRegenerate = view.findViewById(R.id.dialog_bt_regenerate);
        btRegenerate.setOnClickListener(v -> {
            String s = UtilityFunctions.getRandomText();
            etPassword.getEditText().setText(s);
        });
        return builder;
    }*/

    private boolean isEmpty(){
        boolean et_1 = etTitle.getEditText().getText().toString().trim().isEmpty();
        boolean et_2 = etLoginId.getEditText().getText().toString().trim().isEmpty();
        boolean et_3 = etPassword.getEditText().getText().toString().trim().isEmpty();
        return (!et_1 && !et_2 && !et_3);
    }

}

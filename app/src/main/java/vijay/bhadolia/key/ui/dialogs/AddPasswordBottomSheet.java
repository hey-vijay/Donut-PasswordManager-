package vijay.bhadolia.key.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import vijay.bhadolia.key.Interfaces.AddPasswordListener;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.model.Password;

public class AddPasswordBottomSheet extends BottomSheetDialogFragment implements TextWatcher {
    private static final String TAG = AddPasswordBottomSheet.class.getName();

    TextInputLayout etTitle, etLoginId, etPassword;
    Button btSave;
    ImageView imgAccountIcon;

    private AddPasswordListener mListener;

    private int resourceId = -1;
    private String resourceIconName = "";
    Password password;

    public AddPasswordBottomSheet(AddPasswordListener listener, Password password) {
        mListener = listener;
        if (password != null) {
            this.password = password;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_password, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
        if (password != null) {
            etTitle.getEditText().setText(password.getTitle());
            etLoginId.getEditText().setText(password.getAccountName());
            etPassword.getEditText().setText(password.getPassword());
            Drawable drawable;
            if(!password.getResIconName().isEmpty()){
                if(getContext() == null ){
                    dismiss();
                    return;
                }
                resourceIconName = password.getResIconName();
                resourceId =  getContext().getResources().getIdentifier(resourceIconName , "drawable", getContext().getPackageName());
            } else {
                drawable =  ContextCompat.getDrawable(getContext(), R.drawable.ic_choose_image);
            }

            Glide.with(getContext())
                    .load(resourceId)
                    .error(R.drawable.ic_choose_image)
                    .into(imgAccountIcon);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (null != dialog.getWindow()) {
            if (dialog instanceof BottomSheetDialog) {
                ((BottomSheetDialog) dialog).getBehavior().setSkipCollapsed(true);
                ((BottomSheetDialog) dialog).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) Objects.requireNonNull(getView()).getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    private void setUpView(View view) {
        etTitle = view.findViewById(R.id.tv_layout_1);
        etLoginId = view.findViewById(R.id.tv_layout_2);
        etPassword = view.findViewById(R.id.tv_layout_3);
        btSave = view.findViewById(R.id.dialog_add_bt_save);

        imgAccountIcon = view.findViewById(R.id.accountIcon);

        btSave.setOnClickListener(view1 -> {
            if (mListener != null) {
                if (isEmpty()) {
                    Password _password = new Password(
                            etTitle.getEditText().getText().toString(),
                            etLoginId.getEditText().getText().toString(),
                            etPassword.getEditText().getText().toString(),
                            resourceIconName
                    );
                    if (password != null) {
                        //Set the id of the previous password to update the value in db
                        _password.setId(password.getId());
                        mListener.onSaveButtonClicked(_password, false);
                    } else {
                        mListener.onSaveButtonClicked(_password, true);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "All fields are mandatory!!", Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG, "setUpView: All field are mandatory!!");
                }
            }
        });

        imgAccountIcon.setOnClickListener((view1 -> {
            showIconBottomSheet();
        }));

        //attach text watcher to the edit texts
        Objects.requireNonNull(etTitle.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(etLoginId.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(etPassword.getEditText()).addTextChangedListener(this);
    }

    private void showIconBottomSheet() {
        IconBottomSheet bottomSheet = new IconBottomSheet(getContext(), (iconDrawable, resId) -> {
            imgAccountIcon.setImageDrawable(iconDrawable);
            resourceId = resId;
            resourceIconName = getContext().getResources().getResourceEntryName(resId);
        });
        bottomSheet.show(getParentFragmentManager(), "show_icon_bottom_sheet");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        changeButtonBackground(isEmpty());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //Do nothing
    }

    private void changeButtonBackground(boolean colored) {
        if (colored) {
            btSave.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_button_gradient));
        } else {
            btSave.setBackground(ContextCompat.getDrawable(getContext(), R.color.grey_300));
        }
    }

    private boolean isEmpty() {
        boolean et_1 = etTitle.getEditText().getText().toString().trim().isEmpty();
        boolean et_2 = etLoginId.getEditText().getText().toString().trim().isEmpty();
        boolean et_3 = etPassword.getEditText().getText().toString().trim().isEmpty();
        return (!et_1 && !et_2 && !et_3);
    }

    public void changeIcon(Drawable drawable, int resourceId) {
        imgAccountIcon.setImageDrawable(drawable);
        this.resourceId = resourceId;
    }
}

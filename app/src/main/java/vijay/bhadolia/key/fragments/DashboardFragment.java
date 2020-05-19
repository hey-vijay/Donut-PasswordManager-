package vijay.bhadolia.key.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import vijay.bhadolia.key.Adapter.passwordAdapter;
import vijay.bhadolia.key.DataBase.ItemPassword;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.DataBase.PasswordViewModel;
import vijay.bhadolia.key.Utility.UtilityFunctions;


public class DashboardFragment extends Fragment  {

    private final String TAG = "KeyLog";
    private PasswordViewModel viewModel;

    //Variables for recyclerView
    private RecyclerView mRecyclerView;
    private static passwordAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;

    //ArrayList for passwords
    private List<ItemPassword> passwordList = new ArrayList<>();
    //save the last deleted password for Undo option or
    //use this to get the data while editing the password
    private ItemPassword deletedPassword;

    //Initializing Magic
    private boolean isUnlock;
    private int itemPosition = -1;
    private TextView textView;
    private ImageView noImage;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false );
        init(view);
        setHasOptionsMenu(true);
        buildRecyclerView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPassword();
            }
        });

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        viewModel.getAllPassword().observe(getActivity(), new Observer<List<ItemPassword>>() {
            @Override
            public void onChanged(List<ItemPassword> itemPasswords) {
                Log.e(TAG, "Inside ViewModel observer "+ itemPasswords.size() + itemPasswords.toString());
                passwordList = itemPasswords;
                Log.e(TAG, "Inside ViewModel passwordList "+ passwordList.toString());
                mAdapter.submitList(itemPasswords);
                if (passwordList.isEmpty()) {
                    noImage.setVisibility(View.VISIBLE);
                }else {
                    noImage.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        Log.e(TAG, "Dashboard Fragment inside onPause: ");
        super.onPause();
        relativeLayout.setVisibility(View.INVISIBLE);
    }

    private void init(View view){
        textView =  view.findViewById(R.id.textView);
        noImage = view.findViewById(R.id.imgNoItem);
        fab = view.findViewById(R.id.fab);
        coordinatorLayout = view.findViewById(R.id.dashboard_layout_coordinator);
        relativeLayout = view.findViewById(R.id.dashboard_layout_cover_everything);
        mRecyclerView = view.findViewById(R.id.recyclerView);
    }

    public void setIsUnlock(boolean value){
        /*
        *   value == true only if Correct master password is entered.
        *   value == false if Wrong master password is entered and allow
        *                fake entry in turn on in setting.
        * */
        isUnlock = value;
    }

    private void buildRecyclerView() {
        //Initialising RecyclerView members
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mAdapter = new passwordAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        registerForContextMenu(mRecyclerView);

        //Interface Methods
        mAdapter.setOnItemClickListener(new passwordAdapter.onItemClickListener() {
            @Override
            public void onTouchListener(View v, int position) {
                ImageButton img = v.findViewById(R.id.item_imgbtn_lock);
                img.setBackgroundResource(R.drawable.ic_lock_open);
                textView.setVisibility(View.VISIBLE);
                /*
                *   isUnlock == true when User enter with the correct password
                *   isUnlock == false when User enter with the wrong password
                * */
                if (isUnlock) {
                    String text = viewModel.getCredentials(position);
                    textView.setText(text);
                } else {
                    String FakeId = viewModel.getFakeId();
                    textView.setText(FakeId);
                }
            }
            @Override
            public void onTouchRemoveListener(View v, int position) {
                ImageButton img = v.findViewById(R.id.item_imgbtn_lock);
                img.setBackgroundResource(R.drawable.ic_lock);
                textView.setVisibility(View.INVISIBLE);
                //textView.setText("Your Password");
            }
            @Override
            public void onLongItemClick(View v, int position) {
                itemPosition = position;
                deletedPassword = passwordList.get(position);
                v.showContextMenu(100, 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_new:
                inputPassword();
                return true;
            case R.id.option_setting:
                if(!isUnlock) return true;
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.main_container, new SettingsFragment()
                ).commit();
                return true;
            case R.id.option_generate:
                createRdmPswDialogBox();
                return true;
            case R.id.option_support:
                Toast.makeText(getActivity(), "Thanks for showing your interest.",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.option_re_arrange:
                Toast.makeText(getActivity(), "Will add this functionality soon.😅",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //If isUnlock == false, You can not edit or delete any password.
        if(isUnlock){
            switch (item.getItemId()){
                case R.id.action_delete:
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                    break;
                case R.id.action_edit:
                    showDialog(false, deletedPassword.getId());
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    private AlertDialog AskOption() {
        AlertDialog dialogDelete = new AlertDialog.Builder(getActivity())
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Delete  "+deletedPassword.getTitle()+" ?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deletePassword();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
        return dialogDelete;
    }

    private void deletePassword() {
        if (itemPosition != -1) {
            viewModel.delete(deletedPassword);
            showSnackBar(deletedPassword.getTitle());
        }
    }

    private void updateList(String label, String AccName, String AccPassword) {
        if (AccName.isEmpty() || AccPassword.isEmpty() || label.isEmpty()) {
            Toast.makeText(getActivity(), "All Field are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.insert(new ItemPassword(label, AccName, AccPassword));
    }

    private void addDeletedPassword(){
        viewModel.insert(deletedPassword);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.optionmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    //Dialog
    private void createRdmPswDialogBox() {
        //Generate the random password of different length and types
        UtilityFunctions.length = 7;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_generate_password, null);
        final TextInputLayout finalEtLabel = view.findViewById(R.id.dialog_et_label);
        final TextInputLayout etAccountName = view.findViewById(R.id.dialog_et_account_name);
        final TextInputLayout etPassword = view.findViewById(R.id.dialog_et_password);
        builder.setView(view)
                .setTitle("Password Generator!")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String AccName = etAccountName.getEditText().getText().toString();
                        String AccPassword = etPassword.getEditText().getText().toString();
                        String label = finalEtLabel.getEditText().getText().toString();
                        if(AccPassword.isEmpty() || AccName.isEmpty() || label.isEmpty()){
                            Toast.makeText(getActivity(), "All field must be initialized", Toast.LENGTH_SHORT).show();
                        }else{
                            updateList(label, AccName, AccPassword);
                        }
                    }
                });

        etPassword.getEditText().setText(UtilityFunctions.getRandomText());
        Button btRegenerate = view.findViewById(R.id.dialog_bt_regenerate);
        btRegenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = UtilityFunctions.getRandomText();
                etPassword.getEditText().setText(s);
            }
        });
        builder.show();
    }

    //Dialog
    private void inputPassword() {
        showDialog(true, -1);
    }

    private void showDialog(final boolean add, final int id){
        Log.e(TAG, "DashBoard inside show Dialog : " + add);
        /*
        *  If add == true -> open dialog to add new password
        *  If add == false -> open dialog to edit the password
        *                       get the previous data of the password and open the dialog
        * */
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_add_password, null);
        final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogView);

        final Button save = dialogView.findViewById(R.id.dialog_add_bt_save);
        final TextInputLayout etLabel = dialog.findViewById(R.id.tv_layout_1);
        final TextInputLayout etAccountName = dialog.findViewById(R.id.tv_layout_2);
        final TextInputLayout etPassword = dialog.findViewById(R.id.tv_layout_3);

        //Text change listener to change the color of action button
        //TODO: add text change listener to other two element also :etLabel, etAccountName
        etPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    save.setBackground(getResources().getDrawable(R.color.darkGray));
                }else{
                    save.setBackground(getResources().getDrawable(R.color.cyan));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if(!add){
            //show the current data to edit the password.
            etLabel.getEditText().setText(deletedPassword.getTitle());
            etAccountName.getEditText().setText(deletedPassword.getAccountName());
            etPassword.getEditText().setText(deletedPassword.getPassword());
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = etLabel.getEditText().getText().toString().trim();
                String accountName = etAccountName.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString();
                if(label.trim().isEmpty() || accountName.trim().isEmpty()||password.isEmpty()) {
                    Toast.makeText(getActivity(), "All Field are required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(add){
                    //If adding the new password
                    updateList(label, accountName, password);
                    revealShow(dialogView, false, dialog);
                }else{
                    //If any changes made to the previous one
                    ItemPassword itemPassword = new ItemPassword(label, accountName, password);
                    itemPassword.setId(id);
                    viewModel.update(itemPassword);
                    revealShow(dialogView, false, dialog);
                }
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
        //etLabel.getEditText().requestFocus();
    }

    private void showSnackBar(String label) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, label + " Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDeletedPassword();
                        Snackbar snackBar1 = Snackbar.make(coordinatorLayout, "Undo successful", Snackbar.LENGTH_SHORT);
                        snackBar1.getView().setBackgroundColor(Color.DKGRAY);
                        snackBar1.show();
                    }
                })
                .setActionTextColor(Color.RED);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(Color.WHITE);
        TextView textView = snackView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    // Open the add password dialog with circular animation.
    // unexpected behavior
    private void revealShow(final View dialogView, boolean b, final Dialog dialog){
        final View view = dialogView.findViewById(R.id.add_password_layout);
        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab.getX() + (fab.getWidth()/2));
        int cy = (int) (fab.getY() + ((fab.getHeight()))+76);

        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialogView.findViewById(R.id.tv_layout_1).requestFocus();
                }
            });
        }else{
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(400);
            anim.start();
        }
    }
}
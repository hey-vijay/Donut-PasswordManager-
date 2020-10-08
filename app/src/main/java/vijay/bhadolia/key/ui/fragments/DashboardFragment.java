package vijay.bhadolia.key.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import vijay.bhadolia.key.Interfaces.DeleteConfirmationInterface;
import vijay.bhadolia.key.Interfaces.PasswordClickListener;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.adapter.PasswordAdapter;
import vijay.bhadolia.key.model.Password;
import vijay.bhadolia.key.ui.dialogs.AddPasswordBottomSheet;
import vijay.bhadolia.key.ui.dialogs.DeleteConfirmationDialog;
import vijay.bhadolia.key.ui.dialogs.RandomPasswordDialog;
import vijay.bhadolia.key.util.BlurBackground;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.util.Constants.SHOW_PASSWORD_TYPE;
import vijay.bhadolia.key.util.SmartPreferences;


public class DashboardFragment extends Fragment {

    private final String TAG = DashboardFragment.class.getName();

    private PasswordViewModel viewModel;

    //Variables for recyclerView
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout rlLayoutForBlur;
    private FloatingActionButton fab;
    private PasswordAdapter passwordAdapter;
    private List<Password> passwordList;

    private RelativeLayout layoutShowPassword;
    TextView tvAccountTitle, tvAccountId, tvAccountPassword;
    TextView tvLastOpened;
    CardView cvShowPassword;

    //save the last deleted password for Undo option or
    //use this to get the data while editing the password
    private Password deletedPassword;
    private int itemPosition = -1;

    //Initializing Magic
    private ImageView noImage, imgShowPassword;

    private boolean disableBlur = false;
    private boolean allowCopyToClipBoard = false;

    public DashboardFragment() {
        // Required empty public constructor
    }

    //TODO: Last login

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        setHasOptionsMenu(true);
        buildRecyclerView();
        fab.setOnClickListener(v -> showBottomSheetDialog(null));

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        subscribeUi(viewModel.getPasswordList());
    }

    private void subscribeUi(LiveData<List<Password>> passwordList) {
        if (passwordList != null) {
            passwordList.observe(getViewLifecycleOwner(), passwords -> {
                this.passwordList = passwordList.getValue();
                passwordAdapter.submitList(passwords);
                DashboardFragment.this.passwordList = passwords;

                if (allPasswordAreDeleted()) {
                    noImage.setVisibility(View.VISIBLE);
                } else {
                    noImage.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void init(View view) {
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.activity_title_key));

        noImage = view.findViewById(R.id.imgNoItem);
        imgShowPassword = view.findViewById(R.id.imgShowPassword);
        fab = view.findViewById(R.id.fab);
        layoutShowPassword = view.findViewById(R.id.llShowPassword);
        coordinatorLayout = view.findViewById(R.id.dashboard_layout_coordinator);
        rlLayoutForBlur = view.findViewById(R.id.rl_layoutForBlur);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        tvAccountTitle = view.findViewById(R.id.tvAccountTitle);
        tvAccountId = view.findViewById(R.id.tvAccountId);
        tvAccountPassword = view.findViewById(R.id.tvAccountPassword);
        tvLastOpened = view.findViewById(R.id.tvLastActive);
        cvShowPassword = view.findViewById(R.id.cv_ShowPasswordDialog);

        initVariables();
        showLastSeenDialog();

        tvAccountPassword.setOnLongClickListener((view1 -> {
            if (allowCopyToClipBoard) {
                if (getContext() == null) return true;

                String password = tvAccountPassword.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.label), password);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getContext(), R.string.enable_copy_to_clipboard, Toast.LENGTH_LONG)
                        .show();
            }
            return true;
        }));
    }

    private void showLastSeenDialog() {
        boolean showLastSeen = SmartPreferences.getInstance(getContext()).getValue(Constants.SHOW_LAST_ACTIVE_STATUS, true);
        if(showLastSeen) {
            SmartPreferences.getInstance(getContext()).saveValue(Constants.SHOW_LAST_ACTIVE_STATUS, false);
            String time = SmartPreferences.getInstance(getContext()).getValue(Constants.LAST_ACTIVE_TIME, "");
            saveCurrentTime();
            if(time.isEmpty()) return;
            String text = "Last open : " + time;
            tvLastOpened.setVisibility(View.VISIBLE);
            tvLastOpened.setText(text);
        }else {
            tvLastOpened.setVisibility(View.GONE);
        }
        tvLastOpened.setOnClickListener((view -> tvLastOpened.setVisibility(View.GONE)));
    }

    private void saveCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        Log.d(TAG, "saveCurrentTime: formattedDate " + formattedDate);
        SmartPreferences.getInstance(getContext()).saveValue(Constants.LAST_ACTIVE_TIME, formattedDate);

//        Calendar cc = Calendar.getInstance();
//        int year = cc.get(Calendar.YEAR);
//        int month = cc.get(Calendar.MONTH);
//        int mDay = cc.get(Calendar.DAY_OF_MONTH);
//        int ap = cc.get(Calendar.AM_PM);
//        cc.get(Calendar.DAY_OF_MONTH);
//        Log.d(TAG, "saveCurrentTime: Date " + year + ":" + month + ":" + mDay + " ," + ap);
//        Log.d(TAG, "saveCurrentTime: " + cc.get(Calendar.HOUR_OF_DAY) + ":" +cc.get(Calendar.MINUTE));
    }

    private void initVariables() {
        //Initialize all the variables needed.
        disableBlur = SmartPreferences.getInstance(getContext()).getValue(Constants.DISABLE_BLUR_BACKGROUND, false);
        allowCopyToClipBoard = SmartPreferences.getInstance(getContext()).getValue(Constants.COPY_ENABLE, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        passwordAdapter = new PasswordAdapter(getContext(), passwordClickListener);
        mRecyclerView.setAdapter(passwordAdapter);

        //attach layout manager after the animation
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.item_layout_animation);
        mRecyclerView.setLayoutAnimation(animationController);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallback);
        helper.attachToRecyclerView(mRecyclerView);
        layoutShowPassword.setOnTouchListener(showPasswordTouchListener);
    }

    PasswordClickListener passwordClickListener = new PasswordClickListener() {
        @Override
        public void onClick(int position) {
            showPassword(true, position, SHOW_PASSWORD_TYPE.CENTER_DIALOG.getValue());
        }

        @Override
        public void onLongClick(View view, int position) {
            Toast.makeText(getContext(), R.string.move_up_down, Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onViewItemPressed(int position) {
            //Do nothing for now
        }

        @Override
        public void onViewItemUnpressed(int position) {
            //Do nothing for now
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    RecyclerView.OnTouchListener showPasswordTouchListener = (view, motionEvent) -> {
        if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE || motionEvent.getAction() == MotionEvent.ACTION_UP) {
            showPassword(false, -1, -1);
        }
        return true;
    };


    private void viewVisibility(boolean show, View... views) {
        if (show) {
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showPassword(boolean show, int position, int password_type) {
        Log.d(TAG, "showPassword: " + SHOW_PASSWORD_TYPE.CENTER_DIALOG);

        if (show) {
            //Vibrate
            viewVisibility(false, fab);
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(16);
            Password password = passwordList.get(position);
            tvAccountTitle.setText(password.getTitle());
            tvAccountId.setText(password.getAccountName());
            tvAccountPassword.setText(password.getPassword());
            layoutShowPassword.setVisibility(View.VISIBLE);
            if(!disableBlur) {
                Bitmap bitmap = BlurBackground.getBitmapFromView(rlLayoutForBlur);
                Bitmap fastBlur = BlurBackground.blur(getContext(), bitmap);
                Glide.with(getContext())
                        .load(fastBlur)
                        .into(imgShowPassword);
            }
            //Pop animation
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_slight_zoom);
            cvShowPassword.startAnimation(animation);
        } else {
            layoutShowPassword.setVisibility(View.GONE);
            viewVisibility(true, fab);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_setting:
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.main_container, new SettingsFragment()
                ).commit();
                return true;
            case R.id.action_generate_new_password:
                RandomPasswordDialog dialog = new RandomPasswordDialog(
                        getActivity(),
                        getChildFragmentManager(),
                        (password, newPassword) -> {
                            Log.d(TAG, "onOptionsItemSelected: " + password.toString());
                            addPassword(password, true);
                        });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                return true;
            case R.id.action_support:
                Toast.makeText(getActivity(), "Thanks for showing your interest.",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private DeleteConfirmationInterface deleteConfirmationListener = new DeleteConfirmationInterface() {
        @Override
        public void onPositiveButtonClicked() {
            Log.d(TAG, "onPositiveButtonClicked: ");
            deletePassword();
        }

        @Override
        public void onNegativeButtonClicked() {
            //Do nothing
        }
    };

    private void showDeleteConfirmationDialog(Password deletedPassword) {
        AlertDialog deleteBox = DeleteConfirmationDialog.getDialog(
                getActivity(),
                deletedPassword,
                deleteConfirmationListener);
        deleteBox.setOnShowListener(dialogInterface -> {
            deleteBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_300));
        });
        deleteBox.show();
    }

    private void deletePassword() {
        deletedPassword.setDeleted(true);
        viewModel.update(deletedPassword);
        passwordAdapter.notifyItemChanged(itemPosition);
        showSnackBar(deletedPassword.getTitle());
    }

    private void addDeletedPassword() {
        deletedPassword.setDeleted(false);
        viewModel.update(deletedPassword);
        passwordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.optionmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showBottomSheetDialog(Password password) {
        AddPasswordBottomSheet bottomSheet = new AddPasswordBottomSheet((password_, newPassword) -> {
            Log.d(TAG, "showBottomSheetDialog: password " + password_.toString());
            addPassword(password_, newPassword);
        }, password);
        bottomSheet.show(getParentFragmentManager(), "add_password");
    }

    private void showSnackBar(String label) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, label + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    addDeletedPassword();
                    Snackbar snackBar1 = Snackbar.make(coordinatorLayout, R.string.undo_successful, Snackbar.LENGTH_SHORT);
                    snackBar1.getView().setBackgroundColor(Color.DKGRAY);
                    snackBar1.show();
                })
                .setTextColor(ContextCompat.getColor(getContext(), R.color.blue_400))
                .setActionTextColor(Color.RED);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(Color.WHITE);
        TextView textView = snackView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    //Adding drag and drop functionality
    /*
     * Change the ids of the dragged items and upDate all the password
     * in onPause to save the changes.
     *
     * now Also added swipe functionality
     *
     * */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
            //0,
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (getContext() == null) return;
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightActionIcon(R.drawable.ic_delete_swipe)
                    .addSwipeLeftActionIcon(R.drawable.ic_pencil)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_300))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_300))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            Log.d(TAG, "clearView: ");
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            Log.d(TAG, "onMove: start");
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(passwordList, i, i + 1);

                    int id_1 = passwordList.get(i).getId();
                    int id_2 = passwordList.get(i + 1).getId();
                    passwordList.get(i).setId(id_2);
                    passwordList.get(i + 1).setId(id_1);
                    viewModel.update(passwordList.get(i));
                    viewModel.update(passwordList.get(i + 1));
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(passwordList, i, i - 1);

                    int id_1 = passwordList.get(i).getId();
                    int id_2 = passwordList.get(i - 1).getId();
                    passwordList.get(i).setId(id_2);
                    passwordList.get(i - 1).setId(id_1);
                    viewModel.update(passwordList.get(i));
                    viewModel.update(passwordList.get(i - 1));
                }
            }
            Log.d(TAG, "Helper call " + fromPosition + " to " + toPosition);
            passwordAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Do nothing
            Log.d(TAG, "onSwiped: ");
            int position = viewHolder.getAdapterPosition();
            itemPosition = position;
            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    passwordAdapter.notifyItemChanged(position);
                    deletedPassword = passwordList.get(position);
                    showDeleteConfirmationDialog(deletedPassword);
                    break;
                case ItemTouchHelper.LEFT:
                    passwordAdapter.notifyItemChanged(position);
                    showBottomSheetDialog(passwordList.get(position));
                    break;
            }
        }
    };

    private void addPassword(Password password, boolean editedPassword) {
        if (!editedPassword) {
            viewModel.update(password);
        } else {
            viewModel.insert(password);
        }
    }

    private boolean allPasswordAreDeleted() {
        for(Password password: passwordList) {
            if(!password.getDeleted()) {
                return false;
            }
        }
        return true;
    }

}

/*
*   ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(R.string.drag_and_drop);
            getActivity().getMenuInflater().inflate(R.menu.option_menu_save, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.option_save) {
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode = null;
        }
    };
* */
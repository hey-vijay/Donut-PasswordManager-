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
import vijay.bhadolia.key.util.TimeUtils;


public class DashboardFragment extends Fragment {

    private final String TAG = DashboardFragment.class.getName();

    private PasswordViewModel viewModel;

    //Variables for recyclerView
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout rlLayoutForBlur;
    private FloatingActionButton fab;
    private PasswordAdapter passwordAdapter;

    private RelativeLayout layoutShowPassword;
    TextView tvAccountTitle, tvAccountId, tvPassword;
    TextView tvLastOpened;
    CardView cvShowPassword;

    //save the last deleted password for Undo option or
    //use this to get the data while editing the password
    private Password lastDeletedPassword;
    private int itemPosition = -1;

    //Initializing Magic
    private ImageView noItemImage, imgShowPassword;

    private boolean disableBlur = false;
    private boolean allowCopyToClipBoard = false;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setHasOptionsMenu(true);
        buildRecyclerView();
        fab.setOnClickListener(v -> openInputDialog(null));

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        subscribeUi(viewModel.getPasswordList());
    }

    private void subscribeUi(LiveData<List<Password>> passwords) {
        if (passwords != null) {
            passwords.observe(getViewLifecycleOwner(), passwords_ -> {
                Log.d(TAG, "subscribeUi: " + passwords_.toString());
                passwordAdapter.submitList(passwords_);

                if (passwords_.isEmpty()) {
                    noItemImage.setVisibility(View.VISIBLE);
                } else {
                    noItemImage.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void initView(View view) {
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.activity_title_key));

        noItemImage = view.findViewById(R.id.imgNoItem);
        imgShowPassword = view.findViewById(R.id.imgShowPassword);
        fab = view.findViewById(R.id.fab);
        layoutShowPassword = view.findViewById(R.id.llShowPassword);
        coordinatorLayout = view.findViewById(R.id.dashboard_layout_coordinator);
        rlLayoutForBlur = view.findViewById(R.id.rl_layoutForBlur);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        tvAccountTitle = view.findViewById(R.id.tvAccountTitle);
        tvAccountId = view.findViewById(R.id.tvAccountId);
        tvPassword = view.findViewById(R.id.tvAccountPassword);
        tvLastOpened = view.findViewById(R.id.tvLastActive);
        cvShowPassword = view.findViewById(R.id.cv_ShowPasswordDialog);

        initVariables();
        showLastSeenDialog();

        tvPassword.setOnLongClickListener((view1 -> {
            if (allowCopyToClipBoard) {
                if (getContext() == null) return true;

                String password = tvPassword.getText().toString();
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
            Long timeInMillis = SmartPreferences.getInstance(getContext()).getValue(Constants.LAST_ACTIVE_TIME, 0L);
            saveCurrentTime();
            if(timeInMillis == 0) return;
            String text = TimeUtils.getTimeDifference(timeInMillis);
            tvLastOpened.setVisibility(View.VISIBLE);
            tvLastOpened.setText(text);
        }else {
            tvLastOpened.setVisibility(View.GONE);
        }
        tvLastOpened.setOnClickListener((view -> tvLastOpened.setVisibility(View.GONE)));
    }

    private void saveCurrentTime() {
        Long currentTimeMillis = System.currentTimeMillis();
        Log.d(TAG, "saveCurrentTime: formattedDate " + currentTimeMillis);
        SmartPreferences.getInstance(getContext()).saveValue(Constants.LAST_ACTIVE_TIME, currentTimeMillis);

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

    PasswordClickListener passwordClickListener = position -> showPassword(true, position, SHOW_PASSWORD_TYPE.CENTER_DIALOG.getValue());

    @SuppressLint("ClickableViewAccessibility")
    RecyclerView.OnTouchListener showPasswordTouchListener = (view, motionEvent) -> {
        if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE || motionEvent.getAction() == MotionEvent.ACTION_UP) {
            showPassword(false, -1, -1);
        }
        return true;
    };

    private void changeViewVisibility(boolean show, View... views) {
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
        if (show) {
            changeViewVisibility(false, fab);
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(16);
            Password password = passwordAdapter.getItem(position);
            tvAccountTitle.setText(password.getTitle());
            tvAccountId.setText(password.getAccountName());
            tvPassword.setText(password.getPassword());
            layoutShowPassword.setVisibility(View.VISIBLE);
            //Pop animation
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_slight_zoom);
            cvShowPassword.startAnimation(animation);
            if(!disableBlur) {
                Bitmap bitmap = BlurBackground.getBitmapFromView(rlLayoutForBlur);
                Bitmap fastBlur = BlurBackground.blur(getContext(), bitmap);
                Glide.with(getContext())
                        .load(fastBlur)
                        .into(imgShowPassword);
            }
        } else {
            layoutShowPassword.setVisibility(View.GONE);
            changeViewVisibility(true, fab);
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
        lastDeletedPassword.setDeleted(true);
        viewModel.update(lastDeletedPassword);
        passwordAdapter.notifyItemChanged(itemPosition);
        showSnackBar(lastDeletedPassword.getTitle());
    }

    private void restoreDeletedPassword() {
        lastDeletedPassword.setDeleted(false);
        viewModel.update(lastDeletedPassword);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.optionmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //  @password that need to be edit
    private void openInputDialog(Password password) {
        AddPasswordBottomSheet bottomSheet = new AddPasswordBottomSheet((password_, isNewPassword) -> {
            Log.d(TAG, "showBottomSheetDialog: password " + password_.toString());
            addPassword(password_, isNewPassword);
        }, password);
        bottomSheet.show(getParentFragmentManager(), "add_password");
    }

    private void showSnackBar(String text) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, text + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    restoreDeletedPassword();
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
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(viewHolder != null)
            viewHolder.itemView.setAlpha(0.3f);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            Log.d(TAG, "onMove: start");
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            List<Password> passwordList = passwordAdapter.getPasswordList();
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
            passwordAdapter.notifyItemChanged(position);
            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    lastDeletedPassword = passwordAdapter.getItem(position);
                    showDeleteConfirmationDialog(lastDeletedPassword);
                    break;
                case ItemTouchHelper.LEFT:
                    openInputDialog(passwordAdapter.getItem(position));
                    break;
            }
        }
    };

    private void addPassword(Password password, boolean isNewPassword) {
        if (isNewPassword) {
            viewModel.insert(password);
        } else {
            viewModel.update(password);
        }
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
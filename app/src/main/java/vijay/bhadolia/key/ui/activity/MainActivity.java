package vijay.bhadolia.key.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.ui.fragments.ChangePassword;
import vijay.bhadolia.key.ui.fragments.DummyFragment;
import vijay.bhadolia.key.ui.fragments.SettingsFragment;
import vijay.bhadolia.key.util.Constants;
import vijay.bhadolia.key.ui.fragments.DashboardFragment;
import vijay.bhadolia.key.util.SmartPreferences;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private boolean isAuthenticUser = false;

    //Adding Drawer functionality
    private DashboardFragment mainFragment;
    private DummyFragment dummyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        if (getIntent() != null) {
            Intent intent = getIntent();
            isAuthenticUser = intent.getBooleanExtra(Constants.AUTHENTIC_USER, false);
        }

        /*  if correct password is entered then open the Dashboard Fragment else dummy fragment*/
        mainFragment = new DashboardFragment();
        dummyFragment = new DummyFragment();
        openDashboard(isAuthenticUser);
    }

    private void initView() {
        // Disable ScreenShot and app preview from recent app for security reasons
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void saveCurrentTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current dateTime => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd, MMMM yyyy HH:mm:ss a", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        Log.d(TAG, "saveCurrentTime: formattedDate " + formattedDate);
        SmartPreferences.getInstance(this).saveValue(Constants.LAST_ACTIVE_TIME, formattedDate);

//        Calendar cc = Calendar.getInstance();
//        int year = cc.get(Calendar.YEAR);
//        int month = cc.get(Calendar.MONTH);
//        int mDay = cc.get(Calendar.DAY_OF_MONTH);
//        int ap = cc.get(Calendar.AM_PM);
//        cc.get(Calendar.DAY_OF_MONTH);
//        Log.d(TAG, "saveCurrentTime: Date " + year + ":" + month + ":" + mDay + " ," + ap);
//        Log.d(TAG, "saveCurrentTime: " + cc.get(Calendar.HOUR_OF_DAY) + ":" +cc.get(Calendar.MINUTE));
    }

    public void openDashboard(boolean isAuthenticUser) {
        if(isAuthenticUser) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    mainFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    dummyFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        String name = getSupportFragmentManager().findFragmentById(R.id.main_container).getClass().getSimpleName();
        if(SettingsFragment.class.getSimpleName().equals(name)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    mainFragment).commit();
        } else if(ChangePassword.class.getSimpleName().equals(name)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete, menu);
        menu.setHeaderTitle("Options");
    }

}

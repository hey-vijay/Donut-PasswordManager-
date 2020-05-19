package vijay.bhadolia.key.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.Utility.ConstKeyWord;
import vijay.bhadolia.key.fragments.DashboardFragment;

public class MainActivity extends AppCompatActivity {


    //Initializing Magic
    private boolean isUnlock = false;
    private boolean isFirstTime = true;
    //private String masterUserName;
    private String masterPassword;
    TextView textView;
    ImageView noImage;

    //Adding Drawer functionality
    private DashboardFragment mainFragment;

    private static final String TAG = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        loadData();
        if (getIntent() != null) {
            Intent intent = getIntent();
            isUnlock = intent.getBooleanExtra("isUnlock", false);
        }
        mainFragment = new DashboardFragment();
        openDashboard();
    }

    private void init() {
        textView = findViewById(R.id.textView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noImage = findViewById(R.id.imgNoItem);
    }

    public void openDashboard() {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                mainFragment).commit();
        mainFragment.setIsUnlock(isUnlock);
    }

    @Override
    public void onBackPressed() {
        String name = getSupportFragmentManager().findFragmentById(R.id.main_container).getClass().getSimpleName();
        if(name.equals("DashboardFragment")){
            super.onBackPressed();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    mainFragment).commit();
        }
    }

    @Override
    protected void onPause() {
        isUnlock = false;
        super.onPause();
        saveData();
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete, menu);
        menu.setHeaderTitle("Options");
    }

    public void saveData() {
        Log.d(TAG, "Inside Main activity saveData: " + masterPassword);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstKeyWord.Shared_Pref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ConstKeyWord.IsFirstTime, isFirstTime);
        //editor.putString("userName", masterUserName);
        editor.putString("userPassword", masterPassword);
        editor.apply();
    }

    public void loadData() {
        Log.d(TAG, "loadData: " + masterPassword);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstKeyWord.Shared_Pref, MODE_PRIVATE);
        isFirstTime = sharedPreferences.getBoolean(ConstKeyWord.IsFirstTime, true);
        //masterUserName = sharedPreferences.getString("userName", null);
        masterPassword = sharedPreferences.getString("userPassword", null);
        if (isFirstTime) {
            Intent intent = getIntent();
            masterPassword = intent.getStringExtra(ConstKeyWord.KEY);
            //masterUserName = masterPassword;
            isFirstTime = false;
            Toast.makeText(getBaseContext(), "Welcome", Toast.LENGTH_SHORT).show();
        }
    }

}

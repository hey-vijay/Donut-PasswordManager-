package vijay.bhadolia.key.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vijay.bhadolia.key.R;
import vijay.bhadolia.key.model.Password;

@Database(entities = Password.class, version = 1, exportSchema = false)
public abstract class PasswordDatabase extends RoomDatabase {

    private static PasswordDatabase passwordDatabaseInstance;
    public abstract PasswordDao passwordDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized PasswordDatabase getInstance(Context context){
        if(passwordDatabaseInstance == null){
            passwordDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                    PasswordDatabase.class,
                    context.getString(R.string.app_database_name))
                    .fallbackToDestructiveMigration()
                    .build();

            passwordDatabaseInstance.populateInitialData(context);
        }
        return passwordDatabaseInstance;
    }

    private void populateInitialData(Context context){

        PasswordDatabase.databaseWriteExecutor.execute(()->{
            if(passwordDao().count() == 0) {
                Password password_0 = new Password("Tap me",
                        "You can disable background blur from settings",
                        "Touch and hold to copy Password",
                        getIconName(context, R.drawable.ic_shield));
                Password password_1 = new Password("Right swipe me ->",
                        "Right swipe will delete the password",
                        "",
                        getIconName(context, R.drawable.ic_shield));
                Password password_2 = new Password("<- Left Swipe me",
                        "You can edit your password with left swipe",
                        "you can also change the icon",
                        getIconName(context,R.drawable.ic_shield));

                passwordDao().insertPassword(password_0);
                passwordDao().insertPassword(password_1);
                passwordDao().insertPassword(password_2);
            }
        });

    }

    private String getIconName(Context context, int id) {
        return context.getResources().getResourceEntryName(id);
    }
}

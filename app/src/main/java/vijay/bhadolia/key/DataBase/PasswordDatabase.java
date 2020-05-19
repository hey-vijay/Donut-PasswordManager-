package vijay.bhadolia.key.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = ItemPassword.class, version = 1)
public abstract class PasswordDatabase extends RoomDatabase {
    private static PasswordDatabase passwordDatabaseInstance;
    public abstract PasswordDao passwordDao();

    public static synchronized PasswordDatabase getInstance(Context context){
        if(passwordDatabaseInstance == null){
            passwordDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                    PasswordDatabase.class,
                    "passwordManager")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return passwordDatabaseInstance;
    }
}

package vijay.bhadolia.key.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vijay.bhadolia.key.model.Password;
import vijay.bhadolia.key.util.SmartPreferences;

public class PasswordRepository {

    private static final int INSERT = 1;
    private static final int DELETE = 2;
    private static final int UPDATE = 3;

    private PasswordDao passwordDao;
    private LiveData<List<Password>> allPassword;

    public PasswordRepository(Application application) {
        PasswordDatabase passwordDatabase = PasswordDatabase.getInstance(application);
        passwordDao = passwordDatabase.passwordDao();
        allPassword = passwordDao.getAllPassword();
    }

    /*
    *   deleteAllPassword  -- 70 *******
    * */

    public void insertPassword(Password itemPassword) {
        new PasswordAsyncTask(passwordDao, INSERT).execute(itemPassword);
    }

    public void deletePassword(Password itemPassword) {
        new PasswordAsyncTask(passwordDao, DELETE).execute(itemPassword);
    }

    public void updatePassword(Password itemPassword) {
        new PasswordAsyncTask(passwordDao, UPDATE).execute(itemPassword);
    }

    public LiveData<List<Password>> getAllPassword() {
        return allPassword;
    }

    public void deleteAllPassword(){
        Password dummyPassword = new Password(
                "","","",""
        );
        new PasswordAsyncTask(passwordDao, 70).execute(dummyPassword);
    }

    private static class PasswordAsyncTask extends AsyncTask<Password, Void, Void> {

        private PasswordDao passwordDao;
        private int MODE;

        private PasswordAsyncTask(PasswordDao passwordDao, int MODE){
            this.passwordDao = passwordDao;
            this.MODE = MODE;
        }

        @Override
        protected Void doInBackground(Password... itemPasswords) {
            switch (MODE) {
                case INSERT:
                    passwordDao.insertPassword(itemPasswords[0]);
                    break;
                case DELETE:
                    passwordDao.deletePassword(itemPasswords[0]);
                    break;
                case UPDATE:
                    passwordDao.updatePassword(itemPasswords[0]);
                    break;
                case 70:
                    //deleteAll
                    passwordDao.deleteAllPassword();
                    break;
            }
            return null;
        }
    }

}

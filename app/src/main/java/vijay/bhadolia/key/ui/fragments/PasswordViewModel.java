package vijay.bhadolia.key.ui.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import vijay.bhadolia.key.database.PasswordRepository;
import vijay.bhadolia.key.model.Password;
import vijay.bhadolia.key.util.UtilityFunctions;

public class PasswordViewModel extends AndroidViewModel {

    private PasswordRepository repository;
    private LiveData<List<Password>> passwordList;

    public PasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new PasswordRepository(application);
        passwordList = repository.getAllPassword();
    }

    public void insert(Password password){
        repository.insertPassword(password);
    }

    public void delete(Password password){
        repository.deletePassword(password);
    }

    public void update(Password password){
        repository.updatePassword(password);
    }

    public LiveData<List<Password>> getPasswordList(){
        return passwordList;
    }

    public void deleteAll(){
        repository.deleteAllPassword();
    }

    public String getCredentials(int pos) {
        Password temp = passwordList.getValue().get(pos);
        String psw = temp.getPassword();
        String accName = temp.getAccountName();
        return  "Id :       " + accName + "\n" + "Psw :   " + psw;
    }

    public String getRandomString(int n, boolean specialChar){
        return UtilityFunctions.generateRandomText(n, specialChar);
    }

    public String getFakeId(){
        String randomText = getRandomString(7,true);
        String id = getRandomString(10, false);
        return "Id :        " + id + "@gmail.com\n" + "Psw :    " + randomText;
    }

}
















package vijay.bhadolia.key.DataBase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import vijay.bhadolia.key.Utility.UtilityFunctions;

public class PasswordViewModel extends AndroidViewModel {

    private PasswordRepository repository;
    private LiveData<List<ItemPassword>> allPassword;

    public PasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new PasswordRepository(application);
        allPassword = repository.getAllPassword();
    }

    public void insert(ItemPassword itemPassword){
        repository.insertPassword(itemPassword);
    }

    public void delete(ItemPassword itemPassword){
        repository.deletePassword(itemPassword);
    }

    public void update(ItemPassword itemPassword){
        repository.updatePassword(itemPassword);
    }

    public LiveData<List<ItemPassword>> getAllPassword(){
        return allPassword;
    }

    public void deleteAll(){
        repository.deleteAllPassword();
    }

    public String getCredentials(int pos){
        ItemPassword temp = allPassword.getValue().get(pos);
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
















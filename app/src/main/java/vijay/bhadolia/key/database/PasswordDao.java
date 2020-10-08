package vijay.bhadolia.key.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vijay.bhadolia.key.model.Password;

@Dao
public interface PasswordDao {

    @Insert
    void insertPassword(Password password);

    @Query("SELECT COUNT(*) FROM passwords")
    int count();

    @Query("SELECT COUNT(*) FROM passwords WHERE deleted = 1")
    int countOfDeletedPassword();

    @Delete
    void deletePassword(Password password);

    @Update
    void updatePassword(Password password);

    @Query("SELECT * from passwords ORDER BY id ASC")
    LiveData<List<Password>> getAllPassword();

    @Query("SELECT * FROM passwords WHERE id = :id")
    Password getPassword(int id);

    @Query("DELETE  FROM passwords")
    void deleteAllPassword();
}

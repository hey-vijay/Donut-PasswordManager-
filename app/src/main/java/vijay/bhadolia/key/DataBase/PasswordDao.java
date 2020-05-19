package vijay.bhadolia.key.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PasswordDao {
    @Insert
    void insertPassword(ItemPassword itemPassword);

    @Delete
    void deletePassword(ItemPassword itemPassword);

    @Update
    void updatePassword(ItemPassword itemPassword);

    @Query("SELECT * from passwords ORDER BY id DESC")
    LiveData<List<ItemPassword>> getAllPassword();

    @Query("SELECT * FROM passwords WHERE id = :id")
    ItemPassword getPassword(int id);

    @Query("DELETE  FROM passwords")
    void deleteAllPassword();
}

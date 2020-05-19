package vijay.bhadolia.key.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "passwords")
public class ItemPassword {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "account_name")
    private String accountName;

    @ColumnInfo(name = "password")
    private String password;

    public ItemPassword(String title, String accountName, String password){
        this.title = title;
        this.accountName = accountName;
        this.password = password;
    }
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getPassword(){
        return password;
    }

    public String getAccountName(){
        return accountName;
    }

    public String getTitle(){
        return title;
    }
}

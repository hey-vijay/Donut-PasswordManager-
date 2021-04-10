package vijay.bhadolia.key.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password (

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int = 0,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "account_name")
        var accountName:String,

        @ColumnInfo(name = "password")
        var password:String,

        @ColumnInfo(name = "icon")
        var resIconName:String,

        /* trying something new */
        @ColumnInfo(name = "deleted")
        var deleted:Boolean = false,

        @ColumnInfo(name = "frequency")
        var frequency:Int = -1,

        @ColumnInfo(name = "color")
        var color:Int = -1

) {
        constructor(title: String, accountName: String, password: String, resIconName: String) : this (0, title, accountName, password, resIconName)
}


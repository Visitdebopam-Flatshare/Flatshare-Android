package com.joinflatshare.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.joinflatshare.db.tables.TableFlatshareUser

@Dao
abstract class FlatshareUsersDao {
    @Insert
    abstract fun insertUser(user: ArrayList<TableFlatshareUser>)

    @Query("Select id from TableFlatshareUser")
    abstract fun getFlatshareUser(): List<String>

    @Query("Select * from TableFlatshareUser where id=:id")
    abstract fun getFlatshareUser(id: String): TableFlatshareUser?

    @Query("Delete from TableFlatshareUser where id=:id")
    abstract fun deleteUser(id: String)

    @Query("Delete FROM TableFlatshareUser")
    abstract fun deleteAllFlatshareUsers()

    @Update
    abstract fun updateUser(user: TableFlatshareUser)
}
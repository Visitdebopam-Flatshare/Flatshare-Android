package com.joinflatshare.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.joinflatshare.db.tables.TableSendbirdUser

@Dao
abstract class SendBirdUsersDao {
    @Insert
    abstract fun insertUser(user: ArrayList<TableSendbirdUser>)

    @Query("Select id from TableSendbirdUser")
    abstract fun getAllSendbirdUsers(): List<String>

    @Query("Select * from TableSendbirdUser where name=''")
    abstract fun getSendbirdUsersWithoutName(): List<TableSendbirdUser>

    @Query("Select * from TableSendbirdUser where dp=''")
    abstract fun getSendbirdUsersWithoutImage(): List<TableSendbirdUser>

    @Query("Select * from TableSendbirdUser where dp like ('%https%')")
    abstract fun getSendbirdUsersWithWrongImagePath(): List<TableSendbirdUser>

    @Query("Select * from TableSendbirdUser where name like ('%null%')")
    abstract fun getSendbirdUsersWithNullName(): List<TableSendbirdUser>

    @Query("Delete from TableSendbirdUser where id=:id")
    abstract fun deleteUser(id: String)

    @Query("Delete FROM TableSendbirdUser")
    abstract fun deleteAllSendbirdUsers()

    @Update
    abstract fun updateUser(user: TableSendbirdUser)
}
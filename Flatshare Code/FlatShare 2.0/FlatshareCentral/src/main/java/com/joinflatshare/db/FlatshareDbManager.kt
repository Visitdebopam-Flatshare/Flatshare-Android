package com.joinflatshare.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.DeviceConfigurationDao
import com.joinflatshare.db.daos.FlatshareUsersDao
import com.joinflatshare.db.daos.RequestDao
import com.joinflatshare.db.daos.SendBirdUsersDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.db.migrations.DBMigrations
import com.joinflatshare.db.tables.TableApp
import com.joinflatshare.db.tables.TableDeviceConfiguration
import com.joinflatshare.db.tables.TableFlatshareUser
import com.joinflatshare.db.tables.TableRequests
import com.joinflatshare.db.tables.TableSendbirdUser
import com.joinflatshare.db.tables.TableUser
import com.joinflatshare.utils.helper.CommonMethod
import java.util.concurrent.Executors

@Database(
    entities = [TableDeviceConfiguration::class, TableUser::class, TableApp::class, TableRequests::class, TableSendbirdUser::class,
        TableFlatshareUser::class],
    version = 9,
    exportSchema = false
)
abstract class FlatshareDbManager : RoomDatabase() {

    companion object {
        private var instance: FlatshareDbManager? = null
        private val DB_NAME = "Flatshare DB"

        fun getInstance(ctx: Context): FlatshareDbManager {
            if (instance == null) {
                val dbBuilder = Room.databaseBuilder(
                    ctx.applicationContext, FlatshareDbManager::class.java,
                    DB_NAME
                )/*.fallbackToDestructiveMigration()*/.allowMainThreadQueries()
                    .addMigrations(DBMigrations.migrate_8_9)
                dbBuilder.setQueryCallback({ sqlQuery, bindArgs ->
                    CommonMethod.makeLog(
                        "Query",
                        sqlQuery
                    )
                }, Executors.newSingleThreadExecutor())
                instance = dbBuilder.build()
            }
            return instance!!
        }
    }

    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun userDao(): UserDao
    abstract fun appDao(): AppDao
    abstract fun requestDao(): RequestDao
    abstract fun sendBirdUserDao(): SendBirdUsersDao
    abstract fun flatshareUserDao(): FlatshareUsersDao
}
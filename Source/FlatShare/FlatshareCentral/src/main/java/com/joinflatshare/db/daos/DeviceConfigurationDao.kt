package com.joinflatshare.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joinflatshare.db.tables.TableDeviceConfiguration

@Dao
abstract class DeviceConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(configuration: TableDeviceConfiguration)

    @Query("Select * from TableDeviceConfiguration")
    abstract fun getConfiguration(): TableDeviceConfiguration?

    // Clear Table
    @Query("Delete from TableDeviceConfiguration")
    abstract fun clearConfigurationTable()
}
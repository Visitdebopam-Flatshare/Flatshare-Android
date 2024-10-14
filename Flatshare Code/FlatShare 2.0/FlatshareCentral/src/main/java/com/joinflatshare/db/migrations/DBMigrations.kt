package com.joinflatshare.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 06/09/24
 */
object DBMigrations {
    const val latestDBVersion = 9
    val migration_7_8: Migration = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS TableSendbirdUser (id TEXT NOT NULL, name TEXT, dp TEXT, PRIMARY KEY(id))")
        }
    }

    val migration_8_9: Migration = object : Migration(8,9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS TableFlatshareUser (id TEXT NOT NULL, name TEXT, dp TEXT, PRIMARY KEY(id))")
        }
    }
}
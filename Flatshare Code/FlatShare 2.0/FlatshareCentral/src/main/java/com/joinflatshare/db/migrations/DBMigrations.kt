package com.joinflatshare.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by debopam on 06/09/24
 */
object DBMigrations {
    val migrate_7_8: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS TableSendbirdUser (id TEXT NOT NULL, name TEXT, dp TEXT, PRIMARY KEY(id))")
        }
    }

    val migrate_8_9: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS TableSendbirdUser (id TEXT NOT NULL, name TEXT, dp TEXT, PRIMARY KEY(id))")
            database.execSQL("CREATE TABLE IF NOT EXISTS TableFlatshareUser (id TEXT NOT NULL, name TEXT, dp TEXT, PRIMARY KEY(id))")
        }
    }
}
package com.joinflatshare.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Created by debopam on 17/08/23
 */
object Prefs {
    val PREFS_KEY_APPEARANCE = stringPreferencesKey("APPEARANCE")
    val PREFS_KEY_GET_FLAT_REQUIRED = stringPreferencesKey("GET_FLAT_REQUIRED")
    private const val USER_DATASTORE = "USER_DATASTORE"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

    suspend fun setString(context: Context, key: Preferences.Key<String>, value: String) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    suspend fun getString(context: Context, key: Preferences.Key<String>): String {
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: ""
    }

    suspend fun clearDataStore(context: Context) {
        context.dataStore.edit {
            it.remove(PREFS_KEY_APPEARANCE)
            it.remove(PREFS_KEY_GET_FLAT_REQUIRED)
        }
    }
}
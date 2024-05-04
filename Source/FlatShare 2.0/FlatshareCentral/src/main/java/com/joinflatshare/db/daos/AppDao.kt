package com.joinflatshare.db.daos

import androidx.room.Dao
import androidx.room.Query
import com.google.gson.Gson
import com.joinflatshare.pojo.config.ConfigResponse

@Dao
abstract class AppDao {
    companion object {
        const val AWS_ACCESS_KEY = "AWS_ACCESS_KEY"
        const val AWS_SECRET_KEY = "AWS_SECRET_KEY"
        const val AMAZON_S3_BUCKET_NAME = "AMAZON_S3_BUCKET_NAME"
        const val AMAZON_BASE_URL = "AMAZON_BASE_URL"
        const val FIREBASE_KEY = "FIREBASE_KEY"
        const val SENDBIRD_APPID = "SENDBIRD_APPID"
        const val SENDBIRD_API_TOKEN = "SENDBIRD_API_TOKEN"
        const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
        const val MIXPANEL_TOKEN = "MIXPANEL_TOKEN"
        const val CONFIG_RESPONSE = "CONFIG_RESPONSE"
    }

    // String
    @Query("INSERT OR REPLACE INTO TableApp (keyParam,valueParam) VALUES(:key,null)")
    abstract fun delete(key: String)

    // String
    @Query("INSERT OR REPLACE INTO TableApp (keyParam,valueParam) VALUES(:key,:value)")
    abstract fun insert(key: String, value: String?)

    @Query("Select valueParam from TableApp where keyParam=:key")
    abstract fun get(key: String): String?

    fun insertConfigResponse(response: ConfigResponse) {
        val json = Gson().toJson(response)
        insert(CONFIG_RESPONSE, json)
    }

    fun getConfigResponse(): ConfigResponse? {
        val json = get(CONFIG_RESPONSE)
        return if (json.isNullOrBlank())
            null
        else {
            Gson().fromJson(json, ConfigResponse::class.java)
        }
    }
}
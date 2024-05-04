package com.joinflatshare.api.retrofit

import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 22/08/23
 */
object WebserviceCustomRequestHandler {
    fun getLikeRequestUrl(
        likeType: String,
        connectionType: String,
        id: String,
    ): String {
        return if (likeType == BaseActivity.TYPE_FLAT) {
            "$likeType/likes/add/$id"
        } else "users/likes/$connectionType/add/$id"
    }
}
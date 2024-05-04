package com.joinflatshare.api.retrofit

import com.joinflatshare.FlatShareApplication
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.utils.helper.CommonMethods

/**
 * Created by debopam on 22/08/23
 */
object WebserviceCustomResponseHandler {
    fun handleUserResponse(response: UserResponse?) {
        val userData = response?.data
        if (userData != null) {
            val userId = FlatShareApplication.getDbInstance().userDao()
                .get(UserDao.USER_CONSTANT_USERID)
            if (userData.id == userId) {
                CommonMethods.registerUser(response)
            }
        }
    }

    fun handleFlatResponse(response: FlatResponse?) {
        if (response?.data?.id.equals(
                FlatShareApplication.getDbInstance().userDao()
                    .get(UserDao.USER_CONSTANT_USERID)
            )
        ) {
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_KEY_GET_FLAT_REQUIRED, 0)
            if (response?.data?.name.isNullOrBlank())
                FlatShareApplication.getDbInstance().userDao().updateFlatResponse(response)
        }
    }
}
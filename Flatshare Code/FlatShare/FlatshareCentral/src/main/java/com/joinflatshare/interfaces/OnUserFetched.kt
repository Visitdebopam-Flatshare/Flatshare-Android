package com.joinflatshare.interfaces

import com.joinflatshare.pojo.user.UserResponse

/**
 * Created by debopam on 01/02/24
 */
interface OnUserFetched {
    fun userFetched(resp: UserResponse?)
}
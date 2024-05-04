package com.joinflatshare.utils.permission

import com.joinflatshare.utils.system.Prefs

object PermissionPreferenceUtil {
    fun firstTimeAskingPermission(permission: String, value: Boolean) {
        Prefs.getInstance().setBoolean(permission, value)
    }

    fun isFirstTimeAskingPermission(permission: String): Boolean {
        return Prefs.getInstance().getBoolean(permission, true)
    }
}
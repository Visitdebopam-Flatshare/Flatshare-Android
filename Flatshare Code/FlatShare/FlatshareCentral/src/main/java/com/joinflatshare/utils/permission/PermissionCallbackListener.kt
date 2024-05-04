package com.joinflatshare.utils.permission

interface PermissionCallbackListener {
    fun onPermissionGranted()
    fun onPermissionDisabled()
    fun onPermissionPreviouslyDenied()
    fun onPermissionAsk()
}
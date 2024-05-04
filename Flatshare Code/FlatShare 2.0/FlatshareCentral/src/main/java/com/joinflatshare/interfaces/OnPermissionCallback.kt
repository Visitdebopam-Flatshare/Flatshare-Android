package com.joinflatshare.interfaces

fun interface OnPermissionCallback {
    fun onCallback(granted: Boolean)
}
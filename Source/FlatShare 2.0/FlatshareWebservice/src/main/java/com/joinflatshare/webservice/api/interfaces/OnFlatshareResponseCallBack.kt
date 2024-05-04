package com.joinflatshare.webservice.api.interfaces

import com.debopam.retrofit.OnResponseCallback

/**
 * Created by debopam on 31/01/24
 */
interface OnFlatshareResponseCallBack<T> : OnResponseCallback<T> {
    fun onCallBackError(message: String) {}
    fun onCallBackPayment(count: Int) {}
    fun onSomethingWentWrong() {}
    fun onLogout() {}
    fun onLogMessage(logMessage: String?) {}
    fun onLogConsole(exceptionMessage: String) {}
    fun onRestricted(restrictionExpireTime: String) {}
}
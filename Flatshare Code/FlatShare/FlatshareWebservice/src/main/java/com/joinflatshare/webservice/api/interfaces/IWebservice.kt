package com.joinflatshare.webservice.api.interfaces

import androidx.activity.ComponentActivity

/**
 * Created by debopam on 31/01/24
 */
interface IWebservice {
    fun hasInternet(): Boolean
    fun showProgress(activity: ComponentActivity)
    fun hideProgress(activity: ComponentActivity)
    fun onSomethingWentWrong(activity: ComponentActivity)
    fun onLogout(activity: ComponentActivity)
    fun onLogMessage(message: String?)
    fun onLogConsole(message: String)
    fun onCallBackError(activity: ComponentActivity,message: String)
}
package com.joinflatshare.ui.base.gpsfetcher

/**
 * Created by debopam on 29/10/22
 */
interface GpsHandlerCallback {
    fun onLocationUpdate(latitude: Double, longitude: Double)
    fun onLocationFailed()
}
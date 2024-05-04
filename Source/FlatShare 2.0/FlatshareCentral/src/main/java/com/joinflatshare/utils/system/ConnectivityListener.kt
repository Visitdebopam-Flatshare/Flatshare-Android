package com.joinflatshare.utils.system

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.IntentFilterConstants

/**
 * Created by debopam on 26/10/22
 */
object ConnectivityListener {
    fun initializeNetworkCallback() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val intent = Intent()
                intent.action = IntentFilterConstants.INTENT_FILTER_INTERNET
                intent.putExtra("hasInternet", true)
                LocalBroadcastManager.getInstance(FlatShareApplication.instance)
                    .sendBroadcast(intent)
            }

            override fun onLost(network: Network) {
                val intent = Intent()
                intent.action = IntentFilterConstants.INTENT_FILTER_INTERNET
                intent.putExtra("hasInternet", false)
                LocalBroadcastManager.getInstance(FlatShareApplication.instance)
                    .sendBroadcast(intent)
            }
        }
        val connectivityManager =
            FlatShareApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }


    fun isNetworkOnline(): Boolean {
        var result = false
        val context = FlatShareApplication.instance
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }

    @JvmStatic
    fun checkInternet(): Boolean {
        val isOnline = isNetworkOnline()
        if (!isNetworkOnline()) {
            val intent = Intent()
            intent.action = IntentFilterConstants.INTENT_FILTER_INTERNET
            intent.putExtra("hasInternet", false)
            LocalBroadcastManager.getInstance(FlatShareApplication.instance)
                .sendBroadcast(intent)
        }
        return isOnline
    }
}
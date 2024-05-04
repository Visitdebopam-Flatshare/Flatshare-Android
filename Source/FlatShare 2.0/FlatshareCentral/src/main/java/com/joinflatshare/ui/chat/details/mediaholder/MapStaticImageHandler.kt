package com.joinflatshare.ui.chat.details.mediaholder

import com.bumptech.glide.Glide
import com.joinflatshare.constants.GoogleConstants
import com.joinflatshare.interfaces.OnFileFetched
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapStaticImageHandler(private val activity: BaseActivity) {

    fun getMapImage(latitude: Double, longitude: Double, callback: OnFileFetched) {
        val url =
            StringBuilder("https://maps.googleapis.com/maps/api/staticmap?")
        url.append("center=$latitude,$longitude")
        url.append("&zoom=14")
        url.append("&scale=1")
        url.append("&format=jpg")
        url.append("&size=400x200")
        url.append("&markers=color:red|size:mid|$latitude,$longitude")
        getApiKey { text ->
            url.append("&key=$text")
            CommonMethod.makeLog("Static Map", url.toString())
            callApi(url.toString(), callback)
        }
    }

    private fun callApi(url: String, callback: OnFileFetched) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = Glide.with(activity).asFile().load(url).submit().get()
            if (file != null && file.exists())
                callback.onFetched(file)
            else CommonMethod.makeToast( "Failed to get map")
        }
    }

    private fun getApiKey(callback: OnStringFetched) {
        if (GoogleConstants.GOOGLE_API_KEY.isEmpty()) {
            GoogleConstants.initialiseGoogleSdk(activity, object : OnStringFetched {
                override fun onFetched(text: String?) {
                    if (text.equals("1"))
                        callback.onFetched(GoogleConstants.GOOGLE_API_KEY)
                    else CommonMethod.makeToast( "Failed to get map")
                }
            })
        } else callback.onFetched(GoogleConstants.GOOGLE_API_KEY)
    }
}
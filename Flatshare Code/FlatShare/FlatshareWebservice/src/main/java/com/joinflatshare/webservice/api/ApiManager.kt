package com.joinflatshare.webservice.api

import androidx.activity.ComponentActivity
import com.debopam.retrofit.RetrofitHandler
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.webservice.api.interfaces.ApiInterface
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

internal class ApiManager {

    companion object {
        fun getApiInterface(): ApiInterface {
            return RetrofitHandler.getRetrofitClient(ApiInterface::class.java)
        }
    }

    fun callApi(
        activity: ComponentActivity,
        observable: Observable<retrofit2.Response<ResponseBody>>,
        callback: OnFlatshareResponseCallBack<retrofit2.Response<ResponseBody>>?
    ) {
        if (WebserviceManager.uiWebserviceHandler.hasInternet()) {
            WebserviceManager.uiWebserviceHandler.showProgress(activity)
            RetrofitHandler.getObservable(
                observable,
                object : com.debopam.retrofit.OnResponseCallback<retrofit2.Response<ResponseBody>> {
                    override fun onApiCallBack(response: retrofit2.Response<ResponseBody>) {
                        sendResponse(activity, response, callback)
                    }

                    override fun onError(throwable: Throwable) {
                        handleError(activity, throwable, callback)
                    }

                })
        }
    }

    fun callApiWithoutProgress(
        activity: ComponentActivity,
        observable: Observable<retrofit2.Response<ResponseBody>>,
        callback: OnFlatshareResponseCallBack<retrofit2.Response<ResponseBody>>?
    ) {
        if (WebserviceManager.uiWebserviceHandler.hasInternet()) {
            RetrofitHandler.getObservable(
                observable,
                object : com.debopam.retrofit.OnResponseCallback<retrofit2.Response<ResponseBody>> {
                    override fun onApiCallBack(response: retrofit2.Response<ResponseBody>) {
                        sendResponse(activity, response, callback)
                    }

                    override fun onError(throwable: Throwable) {
                        handleError(activity, throwable, callback)
                    }

                })
        }
    }

    private fun sendResponse(
        activity: ComponentActivity,
        response: retrofit2.Response<ResponseBody>,
        callback: OnFlatshareResponseCallBack<retrofit2.Response<ResponseBody>>?
    ) {
        WebserviceManager.uiWebserviceHandler.hideProgress(activity)
        val resp = response.body()?.string()
        if (resp.isNullOrEmpty()) {
            callback?.onError(Throwable("Response cannot be null. Something went wrong."))
        } else
            callback?.onResponseCallBack(resp)
    }

    private fun handleError(
        activity: ComponentActivity,
        throwable: Throwable,
        callback: OnFlatshareResponseCallBack<retrofit2.Response<ResponseBody>>?
    ) {
        WebserviceManager.uiWebserviceHandler.hideProgress(activity)
        if (throwable is HttpException) {
            var url: String
            try {
                url = (throwable.response()!!.raw() as Response).request.url.toString()
            } catch (ex: Exception) {
                url = ""
            }
            val code = throwable.code()
            val exception = throwable.message()
            val message = "Api failed with error code $code and exception $exception"
            callback?.onLogConsole(message)
            var apiMessage = ""
            try {
                when (code) {
                    ConfigConstants.API_ERROR_CODE_WEBSERVICE,
                    ConfigConstants.API_ERROR_CODE_BAD_REQUEST -> {
                        val json = throwable.response()?.errorBody()?.string()
                        if (json != null) {
                            val jObject = JSONObject(json)
                            val data = jObject.optJSONObject("data")
                            if (data != null) {
                                // Contains restriction error
                                val serverTime = data.optString("restrictionUntil")
                                callback?.onRestricted(serverTime)
                            } else {
                                // Regular error
                                apiMessage = jObject.optString("message")
                                if (apiMessage.isNullOrBlank()) {
                                    callback?.onSomethingWentWrong()
                                } else {
                                    callback?.onCallBackError(apiMessage)
                                }
                            }
                        }
                    }

                    ConfigConstants.API_ERROR_CODE_NOT_FOUND -> {
                        val json = throwable.response()?.errorBody()?.string()
                        val jObject = JSONObject(json)
                        apiMessage = jObject.optString("message")
                        if (!apiMessage.isNullOrBlank()) {
                            if (apiMessage == "API Token Required.") {
                                callback?.onLogout()
                                return
                            }
                        }
                        callback?.onSomethingWentWrong()
                    }

                    ConfigConstants.API_ERROR_CODE_RESTRICT -> {
                        val json = throwable.response()?.errorBody()?.string()
                        val jObject = JSONObject(json)
                        val receivedLikesCount = jObject.optInt("count")
                        callback?.onCallBackPayment(receivedLikesCount)
                    }

                    ConfigConstants.API_ERROR_CODE_LOGOUT -> callback?.onLogout()
                    else -> {
                        callback?.onSomethingWentWrong()
                    }
                }
            } catch (ex: Exception) {
                callback?.onSomethingWentWrong()
            }
            // Logging Error
            if (code != ConfigConstants.API_ERROR_CODE_RESTRICT) {
                val errorReason =
                    "Error Code $code.\nApi Url $url.\nApi Exception $exception.\nApi Message $apiMessage"
                callback?.onLogMessage(errorReason)
            }
        } else {
            callback?.onSomethingWentWrong()
            callback?.onLogMessage(throwable.message)
        }
    }

    /* private fun handleRestrictionError(
         activity: ComponentActivity,
         throwable: Throwable,
         callback: OnFlatshareResponseCallBack<retrofit2.Response<ResponseBody>>?
     ) {
         WebserviceManager.uiWebserviceHandler.hideProgress(activity)
         if (throwable is HttpException && throwable.code() == 400) {
             val json = throwable.response()?.errorBody()?.string()
             if (json != null) {
                 val jObject = JSONObject(json)
                 val data = jObject.optJSONObject("data")
                 val serverTime = data?.optString("restrictionUntil")
                 val currentTime = System.currentTimeMillis()
                 if (serverTime != null) {
                     val restrictionTime = DateUtils.getServerDateInMillis(serverTime)
                     val displayHour: String
                     val diff =
                         if (restrictionTime > currentTime) (restrictionTime - currentTime) else (currentTime - restrictionTime)
                     if (diff < android.text.format.DateUtils.DAY_IN_MILLIS) {
                         displayHour = "${
                             Math.floor(
                                 TimeUnit.MILLISECONDS.toHours(diff).toDouble()
                             ).toInt()
                         } hours"
                     } else {
                         displayHour = "${
                             Math.floor(
                                 TimeUnit.MILLISECONDS.toDays(diff).toDouble()
                             ).toInt()
                         } days"
                     }
                     callback?.onCallBackError("Your profile has been restricted. You can like/send chat request again after $displayHour.")
                     return
                 }
             }
         }
         handleError(activity,throwable, callback)
     }*/

    /*private fun handlePaymentError(
        throwable: Throwable,
        purchase: Purchase?,
        callback: OnResponseCallback<Any?>?
    ) {
        if (throwable is HttpException) {
            var url: String
            try {
                url = (throwable.response()!!.raw() as Response).request.url.toString()
            } catch (ex: Exception) {
                url = ""
            }
            val code = throwable.code()
            val exception = throwable.message()
            CommonMethods.makeLog(TAG, "Api failed with error code $code and exception $exception")
            var apiMessage = ""
            try {
                when (code) {
                    ConfigConstants.API_ERROR_CODE_WEBSERVICE,
                    ConfigConstants.API_ERROR_CODE_BAD_REQUEST -> {
                        val json = throwable.response()?.errorBody()?.string()
                        val jObject = JSONObject(json)
                        apiMessage = jObject.optString("message")
                        if (apiMessage.isNullOrBlank()) {
                            callback?.onSomethingWentWrong()
                        } else {
                            callback?.onCallBackError(apiMessage)
                        }
                        if (purchase != null)
                            MixpanelUtils.onPurchaseFailure(
                                purchase.originalJson,
                                code,
                                exception,
                                apiMessage
                            )
                    }

                    ConfigConstants.API_ERROR_CODE_LOGOUT -> callback?.onLogout()
                    else -> {
                        callback?.onSomethingWentWrong()
                    }
                }
            } catch (ex: Exception) {
                callback?.onSomethingWentWrong()
            }
            // Logging Error
            val errorReason =
                "Error Code $code.\nApi Url $url.\nApi Exception $exception.\nApi Message $apiMessage"
            callback?.onLogMessage(errorReason)
        } else {
            callback?.onSomethingWentWrong()
            callback?.onLogMessage(throwable.message)
        }
    }*/
}
package com.joinflatshare.webservice.api

import androidx.activity.ComponentActivity
import com.joinflatshare.pojo.user.AdhaarOtp
import com.joinflatshare.pojo.user.AdhaarRequest
import com.joinflatshare.pojo.user.User
import com.joinflatshare.webservice.api.interfaces.IWebservice
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response


/**
 * Created by debopam on 17/08/23
 */
class WebserviceManager {
    private val apiManager = ApiManager()

    companion object {
        lateinit var uiWebserviceHandler: IWebservice
    }

    fun getCityNameForLocation(
        activity: ComponentActivity,
        latitude: String?,
        longitude: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getCityNameForLocation(
            latitude, longitude
        )
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun sendOtp(
        activity: ComponentActivity,
        phone: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().sendOtp(phone)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun login(
        activity: ComponentActivity,
        requestBody: com.joinflatshare.pojo.user.OtpRequest,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().login(requestBody)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getProfile(
        showProgress: Boolean,
        activity: ComponentActivity,
        phoneNo: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getProfile(phoneNo)
        if (showProgress)
            handleApiRequestWithProgress(activity, observable, callback)
        else handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun updateProfile(
        showProgress: Boolean,
        activity: ComponentActivity,
        user: User?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().updateProfile(user)
        if (showProgress)
            handleApiRequestWithProgress(activity, observable, callback)
        else handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun sendAadharOtp(
        activity: ComponentActivity,
        requestBody: AdhaarRequest,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().requestAadharOtp(requestBody)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun verifyAadhaar(
        activity: ComponentActivity,
        requestBody: AdhaarOtp,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().verifiyAdhaarOtp(requestBody)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun sendAccountDeletionOtp(
        activity: ComponentActivity,
        phone: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().requestAccountDeletionOtp(phone)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun deleteAccount(
        activity: ComponentActivity,
        otp: String,
        requestBody: AdhaarOtp,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().deleteAccount(otp, requestBody)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getFlatRequests(
        activity: ComponentActivity,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getFlatRequests()
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getChatRequests(
        activity: ComponentActivity,
        type: String?, callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getChatRequests(type)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun sendChatRequest(
        activity: ComponentActivity,
        type: String,
        id: String,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val url = "users/connections/$type/request/$id"
        val observable = ApiManager.getApiInterface().sendChatRequest(url)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun sendChatRequestResponse(
        activity: ComponentActivity,
        isAccept: Boolean,
        type: String,
        id: String,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val url = if (isAccept)
            "users/connections/$type/request/accept/$id"
        else "users/connections/$type/request/reject/$id"
        val observable = ApiManager.getApiInterface().sendChatRequestResponse(url)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun getMutualContacts(
        activity: ComponentActivity,
        type: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getMutualContacts(type)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }


    fun getFlat(
        showProgress: Boolean,
        activity: ComponentActivity,
        flatId: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getFlat(flatId)
        if (showProgress)
            handleApiRequestWithProgress(activity, observable, callback)
        else handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun getFlatRecommendation(
        activity: ComponentActivity,
        pageNo: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getFlatRecommendations(pageNo)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getUserRecommendation(
        activity: ComponentActivity,
        pageNo: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getUserRecommendations(pageNo)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getFhtRecommendation(
        activity: ComponentActivity,
        pageNo: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getFhtRecommendations(pageNo)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getDateRecommendation(
        activity: ComponentActivity,
        pageNo: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getDateRecommendations(pageNo)
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun getLikesSent(
        activity: ComponentActivity,
        showProgress: Boolean,
        type: String?,
        pageNo: Int?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getLikesSent(
            type, pageNo
        )
        if (showProgress)
            handleApiRequestWithProgress(activity, observable, callback)
        else handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun getLikesReceived(
        activity: ComponentActivity,
        type: String?,
        pageNo: Int?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().getLikesReceived(
            type, pageNo
        )
        handleApiRequestWithProgress(activity, observable, callback)
    }

    fun addLike(
        activity: ComponentActivity,
        url: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().addLike(url)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }

    fun rejectLike(
        activity: ComponentActivity,
        url: String?,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        val observable = ApiManager.getApiInterface().rejectLike(url)
        handleApiRequestWithoutProgress(activity, observable, callback)
    }


    private fun handleApiRequestWithProgress(
        activity: ComponentActivity,
        observable: Observable<Response<ResponseBody>>,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        apiManager.callApi(activity, observable, callback)
    }

    private fun handleApiRequestWithoutProgress(
        activity: ComponentActivity,
        observable: Observable<Response<ResponseBody>>,
        callback: OnFlatshareResponseCallBack<Response<ResponseBody>>
    ) {
        apiManager.callApiWithoutProgress(activity, observable, callback)
    }
}
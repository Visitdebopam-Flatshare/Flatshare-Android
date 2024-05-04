package com.joinflatshare.api.retrofit

import com.android.billingclient.api.Purchase
import com.debopam.flatshareprogress.DialogCustomProgress
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.api.retrofit.RetrofitClient.Companion.getClient
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.constants.UrlConstants.DEEPLINK_BASE_URL
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.pojo.flat.CreateFlatRequest
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.invite.RequestInvite
import com.joinflatshare.pojo.invite.RequestSavedContacts
import com.joinflatshare.pojo.purchase.PurchaseRequest
import com.joinflatshare.pojo.user.AdhaarOtp
import com.joinflatshare.pojo.user.AdhaarRequest
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.base.BaseActivity.TYPE_FLAT
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.ConnectivityListener
import com.joinflatshare.utils.system.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Math.floor
import java.util.concurrent.TimeUnit

class ApiManager() {
    private lateinit var prefs: Prefs
    private var activity: ApplicationBaseActivity? = null
    private var isLoaderShownByApimanager = false

    private val TAG = ApiManager::class.java.simpleName

    constructor(activity: ApplicationBaseActivity) : this() {
        this.activity = activity
    }


    fun showProgress() {
        DialogCustomProgress.showProgress(activity!!)
    }

    fun hideProgress() {
        DialogCustomProgress.hideProgress(activity!!)
    }

    fun requestInvite(from: String, id: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.requestInvite(
                RequestInvite(from, id)
            )!!.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun verifiyAdhaar(adhaarRequest: AdhaarRequest, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.verifiyAdhaar(adhaarRequest)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun verifiyAdhaarOtp(adhaarOtp: AdhaarOtp, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.verifiyAdhaarOtp(adhaarOtp)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun heartBeat(onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            CompositeDisposable().add(getClient()?.heartbeat()!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    // INVITE
    fun inviteToApp(phone: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.inviteToApp(phone)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun addFriends(phone: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.addFriends(phone)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun getInvitedStatus(
        type: String, request: InvitedRequest, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (request.requester.isBlank()) request.requester =
            FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID)!!
        if (ConnectivityListener.checkInternet()) {
            CompositeDisposable().add(getClient()?.getInvitedStatus(type, request)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    onResponseCallback.oncallBack(trendsResponse)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun sendContacts(request: RequestSavedContacts, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            CompositeDisposable().add(getClient()?.sendContacts(request)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    onResponseCallback.oncallBack(trendsResponse)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    // Requests
    fun sendConnectionRequest(
        showProgress: Boolean,
        type: String, id: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            if (showProgress)
                showProgress()
            isLoaderShownByApimanager = true
            val url = "users/connections/$type/request/$id"
            CompositeDisposable().add(getClient()?.requestConnection(url)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleRestrictionError(throwable, onResponseCallback) })
        }
    }

    fun acceptConnection(
        id: String, type: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        // TODO Fix first
        /*if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            val url = "users/connections/$type/request/accept/$id"
            CompositeDisposable().add(getClient()?.acceptConnection(url)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }*/
    }

    fun rejectConnection(
        id: String, type: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        // TODO Fix first
        /*if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            val url = "users/connections/$type/request/reject/$id"
            CompositeDisposable().add(getClient()?.rejectConnection(url)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }*/
    }

    fun removeConnection(
        id: String, type: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            val url = "users/connections/$type/request/remove/$id"
            CompositeDisposable().add(getClient()?.removeConnection(url)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun acceptFlatInvitation(
        id: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        // TODO Fix first
        /*if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.acceptFlatRequest(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }*/
    }

    fun rejectFlatInvitation(
        id: String, onResponseCallback: OnResponseCallback<Any?>
    ) {

        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.rejectFlatRequest(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun acceptFriendRequest(
        id: String, onResponseCallback: OnResponseCallback<Any?>
    ) {
        // TODO Fix first
        /*if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.acceptFriendRequest(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }*/
    }

    fun rejectFriendRequest(
        id: String, onResponseCallback: OnResponseCallback<Any?>
    ) {

        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.rejectFriendRequest(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun removeFriend(id: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.removeFriend(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    // FLAT
    fun createFlat(request: CreateFlatRequest, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.createFlat(request)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun updateFlat(
        showProgress: Boolean, request: MyFlatData, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            if (showProgress) {
                showProgress()
                isLoaderShownByApimanager = true
            }
            request.deepLink = DEEPLINK_BASE_URL
            CompositeDisposable().add(getClient()?.updateFlat(request.id, request)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    if (trendsResponse?.data?.name?.isBlank() == true && trendsResponse.message.isNotBlank()) {
                        hideProgress()
                        AlertDialog.showAlert(activity, trendsResponse.message)
                    } else {
                        prefs.setString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "0")
                        FlatShareApplication.getDbInstance().userDao()
                            .updateFlatResponse(trendsResponse)
                        AppConstants.isFeedReloadRequired = true
                        sendResponse(trendsResponse, onResponseCallback)
                    }
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun addFlatmate(id: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.addFlatmate(id)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun removeFlatmate(phone: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.removeFlatmate(phone)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    run {
                        // TODO Removing the below code for sendbird as it is implemented in backend
                        /*if (AppConstants.isSendbirdLive) {
                            val flatChannel = SendBirdFlatChannel(activity!!)
                            val flatmateChannel = SendBirdFlatmateChannel(activity!!)
                            // Get flat
                            val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                            if (flat != null) {
                                // Leave my flat group
                                flatChannel.removeFlatMember(
                                    phone, flat.mongoId
                                )
                                // Leave all flatmate groups
                                flatmateChannel.removeFlatMember(
                                    flat.mongoId, phone
                                )
                                flatmateChannel.removeUserFromAllFlatmates(
                                    flat.mongoId, phone
                                )
                            }
                        }*/
                        sendResponse(trendsResponse, onResponseCallback)
                    }
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun exploreDisLike(
        showProgress: Boolean,
        likeType: String,
        connectionType: String,
        id: String,
        onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            if (showProgress) {
                showProgress()
                isLoaderShownByApimanager = true
            }
            var like = /*BuildConfig.DOMAIN*/""
            if (likeType.equals(TYPE_FLAT)) {
                like += "$likeType/likes/delete/$id"
            } else like += "users/likes/$connectionType/delete/$id"
            CompositeDisposable().add(getClient()?.exploreDisLike(like)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun revealLikes(type: String, phone: String, onResponseCallback: OnResponseCallback<Any?>) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.revealLikes(type, phone)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun report(
        showProgress: Boolean,
        type: String, reason: Int?, id: String?, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            if (showProgress) {
                showProgress()
                isLoaderShownByApimanager = true
            }
            val url = "$type/report/$reason/$id"
            CompositeDisposable().add(getClient()?.report(url)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handleError(throwable as Throwable, onResponseCallback) })
        }
    }

    fun purchaseProduct(
        request: PurchaseRequest, purchase: Purchase?, onResponseCallback: OnResponseCallback<Any?>
    ) {
        if (ConnectivityListener.checkInternet()) {
            showProgress()
            isLoaderShownByApimanager = true
            CompositeDisposable().add(getClient()?.purchaseOrder(request)!!
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ trendsResponse ->
                    sendResponse(trendsResponse, onResponseCallback)
                }) { throwable -> handlePaymentError(throwable as Throwable, purchase) })
        }
    }

    private fun sendResponse(response: Any?, onResponseCallback: OnResponseCallback<Any?>?) {
        if (isLoaderShownByApimanager)
            hideProgress()
        onResponseCallback?.oncallBack(response)
    }

    // API
    private fun handleError(throwable: Throwable, callback: OnResponseCallback<Any?>?) {
        hideProgress()
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
                            if (activity != null)
                                AlertImageDialog.somethingWentWrong(activity!!)
                        } else {
                            AlertDialog.showAlert(
                                activity, apiMessage, "OK"
                            )
                        }
                    }

                    ConfigConstants.API_ERROR_CODE_RESTRICT -> {
                        val json = throwable.response()?.errorBody()?.string()
                        val jObject = JSONObject(json)
                        val receivedLikesCount = jObject.optInt("count")
                        callback?.oncallBack(receivedLikesCount)
                    }

                    ConfigConstants.API_ERROR_CODE_LOGOUT -> CommonMethod.logout(activity!!)

                    ConfigConstants.API_ERROR_CODE_NOT_FOUND -> {
                        val json = throwable.response()?.errorBody()?.string()
                        val jObject = JSONObject(json)
                        apiMessage = jObject.optString("message")
                        if (!apiMessage.isNullOrBlank()) {
                            if (apiMessage.equals("API Token Required.")) {
                                CommonMethod.logout(activity!!)
                                return
                            }
                        }
                        AlertImageDialog.somethingWentWrong(activity!!)
                    }

                    else -> {
                        if (activity != null)
                            AlertImageDialog.somethingWentWrong(activity!!)
                    }
                }
            } catch (ex: Exception) {
                if (activity != null)
                    AlertImageDialog.somethingWentWrong(activity!!)
            }
            // Logging Error
            if (code != ConfigConstants.API_ERROR_CODE_RESTRICT) {
                val errorReason =
                    "Error Code $code.\nApi Url $url.\nApi Exception $exception.\nApi Message $apiMessage"
                Logger.log(errorReason, Logger.LOG_TYPE_API)
            }
        } else {
            if (activity != null)
                AlertImageDialog.somethingWentWrong(activity!!)
            Logger.log(throwable.message, Logger.LOG_TYPE_API)
        }
    }

    private fun handlePaymentError(throwable: Throwable, purchase: Purchase?) {
        hideProgress()
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
                            if (activity != null)
                                AlertImageDialog.somethingWentWrong(activity!!)
                        } else {
                            AlertDialog.showAlert(
                                activity, apiMessage, "OK"
                            )
                        }
                        if (purchase != null)
                            MixpanelUtils.onPurchaseFailure(
                                purchase.originalJson,
                                code,
                                exception,
                                apiMessage
                            )
                    }

                    ConfigConstants.API_ERROR_CODE_LOGOUT -> CommonMethod.logout(activity!!)
                    else -> {
                        if (activity != null)
                            AlertImageDialog.somethingWentWrong(activity!!)
                    }
                }
            } catch (ex: Exception) {
                if (activity != null)
                    AlertImageDialog.somethingWentWrong(activity!!)
            }
            // Logging Error
            val errorReason =
                "Error Code $code.\nApi Url $url.\nApi Exception $exception.\nApi Message $apiMessage"
            Logger.log(errorReason, Logger.LOG_TYPE_API)
        } else {
            if (activity != null)
                AlertImageDialog.somethingWentWrong(activity!!)
            Logger.log(throwable.message, Logger.LOG_TYPE_API)
        }
    }

    private fun handleRestrictionError(
        throwable: Throwable,
        responseCallback: OnResponseCallback<Any?>
    ) {
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
                            floor(
                                TimeUnit.MILLISECONDS.toHours(diff).toDouble()
                            ).toInt()
                        } hours"
                    } else {
                        displayHour = "${
                            floor(
                                TimeUnit.MILLISECONDS.toDays(diff).toDouble()
                            ).toInt()
                        } days"
                    }
                    hideProgress()
                    AlertDialog.showAlert(
                        activity,
                        "Your profile has been restricted. You can like/send chat request again after $displayHour."
                    )
                    return
                }
            }
        }
        handleError(throwable, responseCallback)
    }
}
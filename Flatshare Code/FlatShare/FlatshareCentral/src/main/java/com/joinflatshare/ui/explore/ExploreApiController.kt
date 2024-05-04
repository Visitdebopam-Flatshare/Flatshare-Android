package com.joinflatshare.ui.explore

import android.content.Intent
import android.view.View
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.api.retrofit.WebserviceCustomResponseHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.explore.RecommendationResponse
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.flat.edit.FlatEditActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ExploreApiController(
    private val activity: ExploreActivity,
    private val viewBind: ActivityExploreBinding
) {
    var isDataFetching = false
    var hasMoreData = false
    var hasMoreFHTData = false
    var currentPage = 0
    var currentFHTPage = 0
    val apiManager: ApiManager = ApiManager(activity)

    fun getRecommendedFlats() {
        isDataFetching = true
        WebserviceManager().getFlatRecommendation(
            activity,
            currentPage.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    isDataFetching = false
                    AppConstants.isFeedReloadRequired = false
                    val resp = Gson().fromJson(response, RecommendationResponse::class.java)
                    hasMoreData = currentPage < resp.lastIndex
                    val data = resp.flatData
                    if (data.isNotEmpty()) {
                        if (currentPage == 0)
                            activity.flatData.clear()
                        currentPage++
                        activity.flatData.addAll(data)
                        activity.binder.showContentData()
                    } else {
                        hasMoreData = false
                        if (currentPage == 0) {
                            // Check if user's flat location is filled up
                            val isLocationFilled =
                                !(AppConstants.loggedInUser?.flatProperties?.preferredLocation.isNullOrEmpty()
                                        || AppConstants.loggedInUser?.flatProperties?.preferredLocation!![0].loc.coordinates.isNullOrEmpty())
                            if (isLocationFilled) {
                                viewBind.txtFlatEmpty.text =
                                    "Sorry, we’ve run out of potential flats."
                                viewBind.btnEditpreferences.visibility = View.VISIBLE
                            } else {
                                viewBind.txtFlatEmpty.text = ""
                                viewBind.btnEditpreferences.visibility = View.GONE
                                AlertDialog.showAlert(
                                    activity,
                                    "",
                                    "Add your preferred flat location to see shared flats that you might like.",
                                    "Add Location",
                                    "Cancel"
                                ) { _, requestCode ->
                                    if (requestCode == 1) {
                                        MixpanelUtils.onButtonClicked("Add Location clicked[on APFL pop-up]")
                                        viewBind.cardPreferences.performClick()
                                    }
                                    viewBind.txtFlatEmpty.text =
                                        "Sorry, we’ve run out of potential flats."
                                    viewBind.btnEditpreferences.visibility = View.VISIBLE
                                }
                            }
                            activity.binder.showEmptyData("")
                        }
                    }
                }

                override fun onCallBackPayment(count: Int) {
                    isDataFetching = false
                    if (ExploreAdapter.shouldShowPaymentGateway) {
                        PaymentHandler.showPaymentForProfiles(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                activity.reloadFeed()
                            }
                        }
                    }
                }
            })
    }

    fun getRecommendedUsers() {
        isDataFetching = true
        WebserviceManager().getUserRecommendation(
            activity,
            currentPage.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    isDataFetching = false
                    AppConstants.isFeedReloadRequired = false
                    val resp = Gson().fromJson(response, RecommendationResponse::class.java)
                    hasMoreData = currentPage < resp.lastIndex
                    val data = resp.userData
                    if (data.isNotEmpty()) {
                        if (currentPage == 0)
                            activity.userData.clear()
                        currentPage++
                        activity.userData.addAll(data)
                        activity.binder.showContentData()
                    } else {
                        if (currentPage == 0) {
                            // Check if user's flat location is filled up
                            val myflat =
                                FlatShareApplication.getDbInstance().userDao().getFlatData()
                            var isLocationFilled = false
                            if (myflat != null) {
                                if (!myflat.flatProperties.location?.loc?.coordinates.isNullOrEmpty())
                                    isLocationFilled = true
                            }
                            if (isLocationFilled) {
                                viewBind.txtFlatEmpty.text =
                                    "Sorry, we’ve run out of potential flatmates."
                                viewBind.btnEditpreferences.visibility = View.VISIBLE
                            } else {
                                viewBind.txtFlatEmpty.text = ""
                                viewBind.btnEditpreferences.visibility = View.GONE
                                AlertDialog.showAlert(
                                    activity,
                                    "",
                                    "Add flat location to see your potential flatmates.",
                                    "Edit Flat",
                                    "Cancel"
                                ) { _, requestCode ->
                                    if (requestCode == 1) {
                                        MixpanelUtils.onButtonClicked("Edit Flat button[on AFL pop-up]")
                                        val intent =
                                            Intent(activity, FlatEditActivity::class.java)
                                        CommonMethod.switchActivity(activity, intent, false)
                                    }
                                    viewBind.txtFlatEmpty.text =
                                        "Sorry, we’ve run out of potential flatmates."
                                    viewBind.btnEditpreferences.visibility = View.VISIBLE
                                }
                            }
                            activity.binder.showEmptyData("")
                        }
                    }
                }

                override fun onCallBackPayment(count: Int) {
                    isDataFetching = false
                    if (ExploreAdapter.shouldShowPaymentGateway) {
                        PaymentHandler.showPaymentForProfiles(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                activity.reloadFeed()
                            }
                        }
                    }
                }
            })
    }

    fun getRecommendedFHTUsers() {
        isDataFetching = true
        WebserviceManager().getFhtRecommendation(
            activity,
            currentPage.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onCallBackPayment(count: Int) {
                    isDataFetching = false
                    if (ExploreAdapter.shouldShowPaymentGateway) {
                        PaymentHandler.showPaymentForProfiles(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                activity.reloadFeed()
                            }
                        }
                    }
                }

                override fun onResponseCallBack(response: String) {
                    isDataFetching = false
                    AppConstants.isFeedReloadRequired = false
                    val resp = Gson().fromJson(response, RecommendationResponse::class.java)
                    hasMoreFHTData = currentFHTPage < resp.lastIndex
                    val data = resp.userData
                    if (data.isNotEmpty()) {
                        if (currentFHTPage == 0)
                            activity.userData.clear()
                        currentFHTPage++
                        activity.userData.addAll(data)
                        activity.binder.showContentData()
                    } else {
                        hasMoreFHTData = false
                        if (currentFHTPage == 0) {
                            // Check if user's flat location is filled up
                            var isLocationFilled = false
                            if (!AppConstants.loggedInUser?.flatProperties?.preferredLocation.isNullOrEmpty()
                                && !AppConstants.loggedInUser?.flatProperties?.preferredLocation!![0].loc.coordinates.isNullOrEmpty()
                            ) {
                                isLocationFilled = true
                            }
                            if (isLocationFilled) {
                                viewBind.txtFlatEmpty.text =
                                    "Sorry, we’ve run out of potential flatmates for you."
                                viewBind.btnEditpreferences.visibility = View.VISIBLE
                            } else {
                                viewBind.txtFlatEmpty.text = ""
                                viewBind.btnEditpreferences.visibility = View.GONE
                                AlertDialog.showAlert(
                                    activity,
                                    "",
                                    "Add your preferred flat location to see members you would want to live with.",
                                    "Add Location",
                                    "Cancel"
                                ) { _, requestCode ->
                                    if (requestCode == 1) {
                                        MixpanelUtils.onButtonClicked("Add Location clicked[on APFL pop-up]")
                                        viewBind.cardPreferences.performClick()
                                    }
                                    viewBind.txtFlatEmpty.text =
                                        "Sorry, we’ve run out of potential flatmates for you."
                                    viewBind.btnEditpreferences.visibility = View.VISIBLE
                                }
                            }
                            activity.binder.showEmptyData("")
                        }
                    }
                }
            })
    }

    fun getRecommendedDateUsers() {
        isDataFetching = true
        WebserviceManager().getDateRecommendation(
            activity,
            currentPage.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onCallBackPayment(count: Int) {
                    isDataFetching = false
                    if (ExploreAdapter.shouldShowPaymentGateway) {
                        PaymentHandler.showPaymentForProfiles(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                activity.reloadFeed()
                            }
                        }
                    }
                }

                override fun onResponseCallBack(response: String) {
                    isDataFetching = false
                    AppConstants.isFeedReloadRequired = false
                    val resp = Gson().fromJson(response, RecommendationResponse::class.java)
                    hasMoreFHTData = currentFHTPage < resp.lastIndex
                    val data = resp.userData
                    if (data.isNotEmpty()) {
                        if (currentFHTPage == 0)
                            activity.userData.clear()
                        currentFHTPage++
                        activity.userData.addAll(data)
                        activity.binder.showContentData()
                    } else {
                        hasMoreFHTData = false
                        if (currentFHTPage == 0) {
                            viewBind.btnEditpreferences.visibility = View.VISIBLE
                            activity.binder.showEmptyData("Sorry, we’ve run out of potential matches for you.")
                        }
                    }
                }
            })
    }
}
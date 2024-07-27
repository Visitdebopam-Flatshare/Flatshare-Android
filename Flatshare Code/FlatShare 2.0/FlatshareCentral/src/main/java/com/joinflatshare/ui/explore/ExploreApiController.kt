package com.joinflatshare.ui.explore

import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.utils.helper.CommonMethod
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
    var currentPage = 0
    val apiManager: ApiManager = ApiManager(activity)

    fun getRecommendedFHTUsers() {
        isDataFetching = true
        WebserviceManager().getFhtRecommendation(
            activity,
            currentPage.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onCallBackPayment(count: Int) {
                    isDataFetching = false
                    /*if (ExploreAdapter.shouldShowPaymentGateway) {
                        PaymentHandler.showPaymentForProfiles(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                activity.reloadFeed()
                            }
                        }
                    }*/
                }

                override fun onResponseCallBack(response: String) {
                    isDataFetching = false
                    AppConstants.isFeedReloadRequired = false
                    val resp = Gson().fromJson(
                        response,
                        com.joinflatshare.pojo.explore.RecommendationResponse::class.java
                    )
                    hasMoreData = currentPage < resp.lastIndex
                    val data = resp.userData
                    if (data.isNotEmpty()) {
                        if (currentPage == 0)
                            activity.userData.clear()
                        currentPage++
                        activity.userData.addAll(data)
                        activity.binder.showContentData()
                        activity.binder.showUser()
                    } else {
                        hasMoreData = false
                        if (currentPage == 0) {
                            // Check if user's flat location is filled up
                            var isLocationFilled = false
                            if (!CommonMethod.isLocationEmpty(AppConstants.loggedInUser?.location)) {
                                isLocationFilled = true
                            }
                            if (isLocationFilled) {
                                activity.binder.showEmptyData(
                                    R.drawable.img_no_feed,
                                    "We’ve run out of potential\nflatmates for you.",
                                    "Edit Preferences"
                                )
                            } else {
                                activity.binder.showEmptyData(R.drawable.img_feed_friends,
                                    "Find flatmates like friends.",
                                    "Add Preferred Location"
                                )
                            }
                        }
                    }
                }
            })
    }
}
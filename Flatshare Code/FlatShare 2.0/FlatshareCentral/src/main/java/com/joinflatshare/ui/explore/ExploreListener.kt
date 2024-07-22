package com.joinflatshare.ui.explore

import android.app.Activity
import android.content.Intent
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.likes.LikeRequest
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.bottomsheet.IncompleteProfileBottomSheet
import com.joinflatshare.ui.preferences.flat.PreferenceActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.webservice.api.ApiManager
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ExploreListener(
    private val activity: ExploreActivity, private val viewBind: ActivityExploreBinding
) : View.OnClickListener {

    init {
        viewBind.btnNoFeed.setOnClickListener(this)
        viewBind.cardPreferences.setOnClickListener(this)
        viewBind.includePagerExplore.imgCross.setOnClickListener(this)
        viewBind.includePagerExplore.imgCheck.setOnClickListener(this)
        viewBind.includePagerExplore.imgSuperCheck.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnNoFeed.id, viewBind.cardPreferences.id -> {
                val intent = Intent(activity, PreferenceActivity::class.java)
                CommonMethod.switchActivity(
                    activity, intent
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        AppConstants.isFeedReloadRequired = true
                    }
                }
            }

            viewBind.includePagerExplore.imgCross.id -> {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion==false) {
                    IncompleteProfileBottomSheet(
                        activity
                    ) { activity.binder.showUser() }
                    return
                }
                activity.apiManager.exploreDisLike(
                    false,
                    BaseActivity.TYPE_FHT,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    activity.userData[0].data!!.id
                ) { response ->
                    activity.userData.removeAt(0)
                    activity.binder.showUser()
                }
            }

            viewBind.includePagerExplore.imgCheck.id -> {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion==false) {
                    IncompleteProfileBottomSheet(
                        activity
                    ) { activity.binder.showUser() }
                    return
                }
                val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                    BaseActivity.TYPE_FHT, ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    activity.userData[0].data!!.id
                )
                val observable = ApiManager.getApiInterface().addLike(
                    likeUrl, LikeRequest(AppConstants.loggedInUser!!.location.loc.coordinates)
                )
                ApiManager().callApi(
                    activity,
                    observable,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            activity.userData.removeAt(0)
                            activity.binder.showUser()
                        }
                    })
            }

            viewBind.includePagerExplore.imgSuperCheck.id -> {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion==false) {
                    IncompleteProfileBottomSheet(
                        activity
                    ) { activity.binder.showUser() }
                    return
                }
                WebserviceManager().sendChatRequest(
                    activity,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    activity.userData[0].data!!.id,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            activity.userData.removeAt(0)
                            activity.binder.showUser()
                        }
                    })
            }
        }
    }
}
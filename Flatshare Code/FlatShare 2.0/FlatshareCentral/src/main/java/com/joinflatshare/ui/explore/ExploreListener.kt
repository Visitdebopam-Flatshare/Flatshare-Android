package com.joinflatshare.ui.explore

import android.app.Activity
import android.content.Intent
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.bottomsheet.GiftBottomSheet
import com.joinflatshare.ui.preferences.PreferenceActivity
import com.joinflatshare.utils.helper.CommonMethod

class ExploreListener(
    private val activity: ExploreActivity, private val viewBind: ActivityExploreBinding
) : View.OnClickListener {

    init {
        viewBind.btnNoFeed.setOnClickListener(this)
        viewBind.cardPreferences.setOnClickListener(this)
        viewBind.btnExploreLoad.setOnClickListener(this)
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.binder.setup()
            viewBind.pullToRefresh.isRefreshing = false
        }
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

            viewBind.btnExploreLoad.id -> {
                activity.apiController?.getRecommendedFHTUsers()
            }

            /*viewBind.includePagerExplore.ll.id -> {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion == false) {
                    IncompleteProfileBottomSheet(
                        activity
                    ) { activity.binder.showUser() }
                    return
                }
                if (activity.userData.isEmpty())
                    return
                val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                    BaseActivity.TYPE_FHT, ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    activity.userData[0].data!!.id
                )
                WebserviceManager().addLike(activity,
                    likeUrl,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            MixpanelUtils.onButtonClicked("Feed Check")
                            activity.userData.removeAt(0)
                            activity.binder.showUser()
                        }
                    })
            }*/

        }
    }
}
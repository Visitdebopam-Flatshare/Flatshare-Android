package com.joinflatshare.ui.profile.details

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.report.DialogReport
import com.joinflatshare.ui.dialogs.share.DialogShare
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 27/05/24
 */
class ProfileDetailsListener(
    private val activity: ProfileDetailsActivity,
    private val viewBind: ActivityProfileDetailsBinding
) : OnClickListener {
    init {
        viewBind.llProfileShare.setOnClickListener(this)
        viewBind.llProfileReport.setOnClickListener(this)
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.apiController?.getProfile()
            viewBind.pullToRefresh.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            viewBind.llProfileShare.id -> {
                DialogShare(
                    activity,
                    "Share User Profile",
                    BaseActivity.TYPE_FHT,
                    null,
                    activity.user
                )
            }

            viewBind.llProfileReport.id -> {
                DialogReport(
                    activity, BaseActivity.TYPE_FHT, activity.userId, null
                ) { text: String ->
                    if (text == "1") {
                        val intent=Intent()
                        intent.putExtra("report",true)
                        activity.setResult(Activity.RESULT_OK,intent)
                        CommonMethod.finishActivity(activity)
                    }
                }
            }
        }
    }
}
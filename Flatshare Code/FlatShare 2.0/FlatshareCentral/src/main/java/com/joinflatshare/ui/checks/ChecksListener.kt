package com.joinflatshare.ui.checks

import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivityCheckListBinding

class ChecksListener(
    private val activity: ChecksActivity,
    private val viewBind: ActivityCheckListBinding
) : View.OnClickListener {
    init {
        viewBind.frameSent.setOnClickListener(this)
        viewBind.frameReceived.setOnClickListener(this)
        viewBind.rlChecks.setOnClickListener(this)
        viewBind.rlSuperChecks.setOnClickListener(this)
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.fetchData()
            viewBind.pullToRefresh.isRefreshing = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.frameSent.id -> {
                if (activity.source == ChecksActivity.SOURCE_SENT)
                    return
                activity.source = ChecksActivity.SOURCE_SENT
                activity.dataBinder.prepareButtons()
                activity.fetchData()
            }

            viewBind.frameReceived.id -> {
                if (activity.source == ChecksActivity.SOURCE_RECEIVED)
                    return
                activity.source = ChecksActivity.SOURCE_RECEIVED
                activity.dataBinder.prepareButtons()
                activity.fetchData()
            }

            viewBind.rlChecks.id -> {
                if (activity.mode == ChecksActivity.MODE_CHECKS)
                    return
                activity.mode = ChecksActivity.MODE_CHECKS
                activity.dataBinder.prepareButtons()
                activity.fetchData()
            }

            viewBind.rlSuperChecks.id -> {
                if (activity.mode == ChecksActivity.MODE_SUPER_CHECKS)
                    return
                activity.mode = ChecksActivity.MODE_SUPER_CHECKS
                activity.dataBinder.prepareButtons()
                activity.fetchData()
            }
        }
    }
}
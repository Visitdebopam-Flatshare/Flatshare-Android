package com.joinflatshare.ui.dialogs.report

import com.joinflatshare.constants.ReportConstants
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.share.DialogShare
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod

class DialogReport(
    private val activity: BaseActivity,
    private var reportType: String,
    private val userId: String?,
    private val flat: MyFlatData?,
    private val callback: OnStringFetched
) {
    init {
        val menu = ArrayList<ModelBottomSheet>()
        if (reportType != BaseActivity.TYPE_FLAT)
            reportType = BaseActivity.TYPE_USER
        when (reportType) {
            BaseActivity.TYPE_FLAT -> {
                menu.add(ModelBottomSheet(0, "Seems like a fake flat."))
                if (activity is ExploreActivity || activity is FlatDetailsActivity) {
                    menu.add(ModelBottomSheet(0, "Seems like a broker."))
                    menu.add(ModelBottomSheet(0, "Inappropriate photos."))
                } else {
                    menu.add(ModelBottomSheet(0, "Asking for brokerage."))
                    menu.add(ModelBottomSheet(0, "Inappropriate photos."))
                    menu.add(ModelBottomSheet(0, "Inappropriate messages."))
                }
                if (DialogShare.isAllFlatDataAvailableToShare(flat))
                    menu.add(ModelBottomSheet(0, "Share ${flat?.name}"))
            }

            else -> {
                menu.add(ModelBottomSheet(0, "Seems like a fake profile."))
                if (activity is ExploreActivity || activity is ProfileDetailsActivity) {
                    menu.add(ModelBottomSheet(0, "Seems like a broker."))
                    menu.add(ModelBottomSheet(0, "Inappropriate photos."))
                } else {
                    menu.add(ModelBottomSheet(0, "Asking for brokerage."))
                    menu.add(ModelBottomSheet(0, "Inappropriate photos."))
                    menu.add(ModelBottomSheet(0, "Inappropriate messages."))
                }
            }
        }


        BottomSheetView(activity, menu) { view, pos ->
            val name = menu[pos].name
            if (name.startsWith("Share")) {
                // Show share dialog
                DialogShare(
                    activity,
                    "Share Flat Profile",
                    BaseActivity.TYPE_FLAT,
                    flat, null
                )
            } else {
                var reportConstant = 0
                var id = ""
                when (reportType) {
                    BaseActivity.TYPE_FLAT -> {
                        reportConstant = ReportConstants.reportFlatMap[name]!!
                        id = flat?.mongoId!!
                    }

                    else -> {
                        reportConstant = ReportConstants.reportUserMap[name]!!
                        id = userId!!
                    }
                }
                report(reportConstant, id)
            }
        }
    }

    private fun report(reason: Int, id: String) {
        activity.apiManager.report(
            true, reportType, reason, id
        ) { response ->
            val resp = response as com.joinflatshare.pojo.BaseResponse
            if (resp.status == 200) {
                // TODO Removing the below code for sendbird as it is implemented in backend
                //                    leaveSendbirdChannel()
                CommonMethod.makeToast(
                    "Reported"
                )
                callback.onFetched("1")
            }
        }
    }
}
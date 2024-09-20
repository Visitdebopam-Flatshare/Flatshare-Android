package com.joinflatshare.ui.bottomsheet

import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetEliteLearnMoreBinding
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetGiftBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.bottomsheet.elite.EliteBottomSheet
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 20/06/24
 */
class GiftBottomSheet(
    private val activity: BaseActivity,
):BottomSheetBaseView(activity) {

    private lateinit var viewBind: DialogBottomsheetGiftBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetGiftBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        init()
        showDialog(dialog)
    }

    private fun init() {
        AppConstants.loggedInUser?.isGiftPopupShown = true
        activity.baseApiController.updateUser(false, AppConstants.loggedInUser, null)
    }
}
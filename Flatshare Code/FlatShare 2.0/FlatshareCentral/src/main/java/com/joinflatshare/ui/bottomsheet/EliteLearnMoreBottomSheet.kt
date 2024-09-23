package com.joinflatshare.ui.bottomsheet

import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetEliteLearnMoreBinding
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 20/06/24
 */
class EliteLearnMoreBottomSheet(
    private val activity: BaseActivity,
    private val user: User
):BottomSheetBaseView(activity) {

    private lateinit var viewBind: DialogBottomsheetEliteLearnMoreBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetEliteLearnMoreBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        init()
        click()
        showDialog(dialog)
    }

    private fun init() {
        viewBind.txtEliteUser.text = "${user?.name?.firstName} is an\nElite member"
        ImageHelper.loadProfileImage(activity, viewBind.imgProfile, viewBind.txtPhoto, user)
    }

    private fun click() {
        viewBind.imgCross.setOnClickListener {
            dismissDialog(dialog)
        }
        viewBind.llLearnMore.setOnClickListener {
            dismissDialog(dialog)
            Handler(Looper.getMainLooper()).postDelayed(
                { PaymentHandler.showPaymentForElite(activity, null) }, 500
            )

        }
    }
}
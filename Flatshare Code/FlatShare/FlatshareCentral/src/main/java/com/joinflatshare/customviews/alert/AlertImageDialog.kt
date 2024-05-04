package com.joinflatshare.customviews.alert

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogAlertImageBinding
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

/**
 * Created by debopam on 26/10/22
 */
object AlertImageDialog {
    private const val autoFadeOutTimeout = 4000L
    private var shouldAutoFadeout = false

    private fun showAlert(
        activity: ComponentActivity,
        img: Int,
        description: String,
        button: String,
        onUiEventClick: OnUiEventClick?
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val viewBind: DialogAlertImageBinding =
            DialogAlertImageBinding.inflate(activity.layoutInflater)
        builder.setView(viewBind.root)
        builder.setCancelable(false)
        val dialog = builder.create()

        // DESC
        if (description.isEmpty()) viewBind.txtAlertDesc.visibility = View.GONE else {
            viewBind.txtAlertDesc.visibility = View.VISIBLE
            viewBind.txtAlertDesc.text = description
        }

        // Image
        viewBind.imgAlert.setImageResource(img)

        // Button
        if (button.isEmpty()) viewBind.btnAlertBlue.visibility = View.GONE else {
            viewBind.btnAlertBlue.visibility = View.VISIBLE
            viewBind.btnAlertBlue.text = button
            viewBind.btnAlertBlue.setOnClickListener {
                onUiEventClick?.onClick(null, 1)
                CommonMethods.dismissDialog(activity, dialog)
            }
        }

        dialog.setCancelable(false)

        CommonMethods.showDialog(activity,dialog)

        if (shouldAutoFadeout) {
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    CommonMethods.dismissDialog(activity, dialog)
                }

            }, autoFadeOutTimeout)
        }
        return dialog
    }

    private fun showAlert(
        activity: ComponentActivity,
        img: Int,
        description: String
    ): AlertDialog {
        return showAlert(activity, img, description, "", null)
    }

    fun somethingWentWrong(activity: ComponentActivity) {
        shouldAutoFadeout = true
        showAlert(activity, R.drawable.img_error, activity.resources.getString(R.string.error))
        Handler(Looper.getMainLooper()).postDelayed(
            { CommonMethod.finishActivity(activity) },
            autoFadeOutTimeout
        )
    }

    fun noInternet(activity: ApplicationBaseActivity) {
        shouldAutoFadeout = false
        showAlert(
            activity,
            R.drawable.img_internet,
            activity.resources.getString(R.string.noInternet)
        )
    }

}
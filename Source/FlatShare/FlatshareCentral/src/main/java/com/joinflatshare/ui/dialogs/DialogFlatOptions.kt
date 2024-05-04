package com.joinflatshare.ui.dialogs

import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.FlatshareCentral.databinding.IncludePopupHeaderBinding
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethods

open class DialogFlatOptions(val activity: BaseActivity, val callback: OnUiEventClick) {
    val viewBind: DialogFlatOptionsBinding
    val holder: IncludePopupHeaderBinding
    val dialog: AlertDialog
    var flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
    var loggedInUser = FlatShareApplication.getDbInstance().userDao().getUser()

    fun setName(name: String) {
        // Header
        holder.txtDialogHeader.text = name
        CommonMethods.showDialog(activity, dialog)
    }

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        holder = IncludePopupHeaderBinding.inflate(activity.layoutInflater)
        viewBind = DialogFlatOptionsBinding.inflate(activity.layoutInflater)
        holder.llPopupHolder.addView(viewBind.root)
        builder.setView(holder.root)
        builder.setCancelable(true)
        dialog = builder.create()

        holder.imgDialogCross.setOnClickListener {
            callback.onClick(null, 0)
            CommonMethods.dismissDialog(activity, dialog)
        }

        holder.btnPopupSave.setOnClickListener {
            callback.onClick(null, 1)
            CommonMethods.dismissDialog(activity, dialog)
        }

        dialog.setOnCancelListener {
            callback.onClick(null, 0)
            CommonMethods.dismissDialog(activity, dialog)
        }

        // content
        holder.viewBg.setOnClickListener {}
    }
}
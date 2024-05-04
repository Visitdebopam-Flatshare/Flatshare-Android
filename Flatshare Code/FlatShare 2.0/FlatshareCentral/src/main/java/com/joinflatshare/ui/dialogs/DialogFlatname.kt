package com.joinflatshare.ui.dialogs

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatnameBinding
import com.joinflatshare.FlatshareCentral.databinding.IncludePopupHeaderBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.AppData
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.flat.CreateFlatRequest
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.system.Prefs

class DialogFlatname(private val activity: BaseActivity, private val callback: OnStringFetched) {
    var viewBind: DialogFlatnameBinding

    init {
        createFlat()
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = IncludePopupHeaderBinding.inflate(activity.layoutInflater)
        viewBind = DialogFlatnameBinding.inflate(activity.layoutInflater)
        holder.llPopupHolder.addView(viewBind.root)
        builder.setView(holder.root)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()

        // Header
        holder.txtDialogHeader.text = "Flat Name"
        holder.imgDialogCross.setOnClickListener {
            CommonMethods.dismissDialog(activity, dialog)
        }

        // content
        holder.viewBg.setOnClickListener {}
        holder.btnPopupSave.visibility = View.GONE
        viewBind.imgTick.setOnClickListener {
            val text = viewBind.edtFlatName.text.toString().trim()
            if (text.isBlank()) CommonMethod.makeToast("Flat name cannot be blank")
            else {
                val size: Int = viewBind.edtFlatName.text.toString().trim().length
                if (size < 4 || size > 20) {
                    CommonMethod.makeToast("Flat name must be 4-20 characters")
                } else {
                    val request = CreateFlatRequest()
                    request.name = text
                    if (AppData.flatData?.rentRange?.startRange != null) request.flatProperties.rentperPerson =
                        AppData.flatData?.rentRange?.startRange!!
                    request.id = AppConstants.loggedInUser?.id!!
                    activity.apiManager.createFlat(
                        request
                    ) { response ->
                        val resp = response as FlatResponse
                        if (resp.data == null || resp.data?.name.isNullOrBlank()) {
                            CommonMethod.makeToast(resp.message)
                        } else {
                            activity.prefs.setString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "0")
                            FlatShareApplication.getDbInstance().userDao().updateFlatResponse(resp)
                            /*SendBirdFlatChannel(activity).createFlatChannel(
                                resp, SendBirdConstants.CHANNEL_TYPE_FLAT, null
                            )*/
                            CommonMethods.dismissDialog(activity, dialog)
                            callback.onFetched("1")
                        }
                    }
                }
            }
        }
//        CommonMethods.showDialog(activity, dialog)
    }

    private fun createFlat() {
        val request = CreateFlatRequest()
        request.name = AppConstants.loggedInUser?.name?.firstName + "'s Flat"
        if (AppData.flatData?.rentRange?.startRange != null)
            request.flatProperties.rentperPerson = AppData.flatData?.rentRange?.startRange!!
        request.id = AppConstants.loggedInUser?.id!!
        activity.apiManager.createFlat(
            request
        ) { response ->
            val resp = response as FlatResponse
            if (resp.data == null || resp.data?.name.isNullOrBlank()) {
                CommonMethod.makeToast(resp.message)
            } else {
                activity.prefs.setString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "0")
                FlatShareApplication.getDbInstance().userDao().updateFlatResponse(resp)
                callback.onFetched("1")
            }
        }
    }
}
package com.joinflatshare.ui.dialogs

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.DialogIncompleteProfileBinding
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.flat.edit.FlatEditActivity
import com.joinflatshare.ui.profile.edit.ProfileEditActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class DialogIncompleteProfile(
    activity: BaseActivity,
    type: String
) {
    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = DialogIncompleteProfileBinding.inflate(activity.layoutInflater)
        builder.setView(holder.root)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()

        // content
        holder.txtFlatEmptyHeader.text =
            "Nobody checks empty profiles ${activity.getEmojiByUnicode(0x1F64A)}"

        when (type) {
            BaseActivity.TYPE_FHT,
            BaseActivity.TYPE_FLAT,
            BaseActivity.TYPE_DATE -> {
                holder.txtFlatEmptyDesc.text =
                    "Complete your profile to send checks and chat requests."
                holder.imgFlat.setImageResource(R.drawable.ic_empty_profile)
                holder.btnEditflat.text = "EDIT PROFILE"
            }

            BaseActivity.TYPE_USER -> {
                holder.txtFlatEmptyDesc.text =
                    "Complete your flat profile to send checks and chat requests."
                holder.imgFlat.setImageResource(R.drawable.ic_empty_flat)
                holder.btnEditflat.text = "EDIT FLAT"
            }
        }

        holder.imgDialogCross.setOnClickListener { CommonMethods.dismissDialog(activity, dialog) }

        holder.btnEditflat.setOnClickListener {
            var intent: Intent? = null
            when (type) {
                BaseActivity.TYPE_FHT,
                BaseActivity.TYPE_FLAT,
                BaseActivity.TYPE_DATE -> {
                    MixpanelUtils.onButtonClicked("Edit Profile clicked[on IMPF pop-up]")
                    intent = Intent(activity, ProfileEditActivity::class.java)
                }

                BaseActivity.TYPE_USER -> {
                    MixpanelUtils.onButtonClicked("Edit Flat button[on IFPF pop-up]")
                    intent = Intent(activity, FlatEditActivity::class.java)
                }
            }
            CommonMethod.switchActivity(activity, intent, false)
            CommonMethods.dismissDialog(activity, dialog)
        }
        CommonMethods.showDialog(activity, dialog)
    }
}
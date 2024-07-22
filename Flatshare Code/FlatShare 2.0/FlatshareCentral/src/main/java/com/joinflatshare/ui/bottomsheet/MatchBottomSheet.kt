package com.joinflatshare.ui.bottomsheet

import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogMatchBinding
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 20/06/24
 */
class MatchBottomSheet(
    private val activity: ApplicationBaseActivity,
    private val user1: User?,
    private val user2: User?
) {
    private lateinit var viewBind: DialogMatchBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogMatchBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        setUserDP()
        click()
        dialog.show()
    }

    private fun setUserDP() {
        ImageHelper.loadProfileImage(activity, viewBind.imgPhoto1, viewBind.txtPhoto1, user1)
        ImageHelper.loadProfileImage(activity, viewBind.imgPhoto2, viewBind.txtPhoto2, user2)
    }

    private fun click() {
        viewBind.imgCrossIncomplete.setOnClickListener {
            dialog.dismiss()
        }
        viewBind.llChat.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(activity, ChatListActivity::class.java)
            CommonMethod.switchActivity(activity, intent, false)
        }
    }
}
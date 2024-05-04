package com.joinflatshare.ui.chat.details

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.debopam.ImagePicker.Companion.with
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatDetailsBinding
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_CAMERA
import com.joinflatshare.ui.chat.details.audioview.ChatDetailsMicListener
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.sendbird.android.message.UserMessage

class ChatDetailsListener(
    private val activity: ChatDetailsActivity,
    private val viewBind: ActivityChatDetailsBinding,
    private val dataBinder: ChatDetailsViewBind
) : View.OnClickListener {
    var micListener: ChatDetailsMicListener? = null

    init {
//        micListener = ChatDetailsMicListener(activity, dataBinder);
        chatSendButtonToggle()
        viewBind.includeChatTopbar.frameTopbarPhoto.setOnClickListener(this)
        viewBind.imgChatMessageSend.setOnClickListener(this)
        viewBind.includeChatTopbar.llChatTopbarIconone.setOnClickListener(this)
        viewBind.includeChatTopbar.llChatTopbarIcontwo.setOnClickListener(this)
        viewBind.includeChatTopbar.llChatTopbarIconthree.setOnClickListener(this)
        viewBind.imgMessageAddmultimedia.setOnClickListener(this)
        viewBind.imgMessageCamera.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.includeChatTopbar.frameTopbarPhoto.id -> {
                if (activity.groupChannel.url.startsWith("USER_")) {
                    val phone =
                        activity.sendBirdChannel.getChannelDisplayUserId(activity.groupChannel)
                    val intent = Intent(activity, ProfileDetailsActivity::class.java)
                    intent.putExtra("phone", phone)
                    CommonMethod.switchActivity(activity, intent, false)
                } else if (activity.groupChannel.url.startsWith("FLAT")) {
                    val phone = activity.sendBirdChannel.getFlatId(activity.groupChannel.url)
                    val intent = Intent(activity, FlatDetailsActivity::class.java)
                    intent.putExtra("phone", phone)
                    CommonMethod.switchActivity(activity, intent, false)
                }
            }
            viewBind.imgChatMessageSend.id -> {
                // Send message
                dataBinder.sendListener.sendMessage()
            }
            viewBind.includeChatTopbar.llChatTopbarIconone.id -> {
                // Icon 1
                CommonMethods.hideSoftKeyboard(activity)
                if (dataBinder.adapter.selectedMessage != null) {
                    // User has selected a message
                    // Reply
                    dataBinder.replyViewBind.show(dataBinder.adapter.selectedMessage)
                } else {
                    // Show three dot options
                    if (viewBind.includeChatMedia.bottomSheetMedia.visibility == View.VISIBLE)
                        dataBinder.mediaBottomSheet.toggleView()
                    ChatDetailsBottomMenu(activity).click(activity.groupChannel)
                }
            }
            viewBind.includeChatTopbar.llChatTopbarIcontwo.id -> {
                CommonMethods.hideSoftKeyboard(activity)
                if (dataBinder.adapter.selectedMessage is UserMessage) {
                    // Copy
                    val clipboard =
                        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(
                        activity.resources.getString(R.string.app_name),
                        dataBinder.adapter.selectedMessage.message
                    )
                    clipboard.setPrimaryClip(clip)
                    CommonMethod.makeToast( "Message copied")
                    activity.onBackPressed()
                }
            }
            viewBind.includeChatTopbar.llChatTopbarIconthree.id -> {
                CommonMethods.hideSoftKeyboard(activity)
                // Delete
                // Delete
                activity.sendBirdMessage.deleteMessage(
                    activity.groupChannel,
                    dataBinder.adapter.selectedMessage
                )
                activity.onBackPressed()
            }
            viewBind.imgMessageAddmultimedia.id -> {
                // Plus button to add media
                CommonMethods.hideSoftKeyboard(activity)
                dataBinder.mediaBottomSheet.toggleView()
            }
            viewBind.imgMessageCamera.id -> {
                if (activity.hasPermission(Manifest.permission.CAMERA)
                    && activity.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    showCameraOption()
                } else activity.requestPermissionsSafely(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), REQUEST_CODE_CAMERA
                )
            }
        }
    }

    fun showCameraOption() {
        with(activity)
            .cameraOnly()
            .compress(1024)
            .crop()
            .start(REQUEST_CODE_CAMERA)
    }

    private fun chatSendButtonToggle() {
        val mHandler = android.os.Handler(Looper.getMainLooper())
        viewBind.edtChatMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (!activity.groupChannel.isTyping) activity.groupChannel.startTyping()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (viewBind.includeChatMedia.bottomSheetMedia.visibility == View.VISIBLE)
                    dataBinder.mediaBottomSheet.toggleView()
            }

            override fun afterTextChanged(s: Editable) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(userStoppedTyping, 3000); // 2 second
                /*if (s.isEmpty()) {
                    viewBind.imgChatMic.visibility = View.VISIBLE
                    viewBind.imgChatMic.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                    viewBind.imgChatMessageSend.setVisibility(View.GONE)
                    viewBind.imgChatMessageSend.animate().scaleX(0f).scaleY(0f).setDuration(100)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                    viewBind.imgMessageCamera.setVisibility(View.VISIBLE)
                    viewBind.imgMessageCamera.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                } else {
                    viewBind.imgChatMic.setVisibility(View.GONE)
                    viewBind.imgChatMic.animate().scaleX(0f).scaleY(0f).setDuration(100)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                    viewBind.imgChatMessageSend.setVisibility(View.VISIBLE)
                    viewBind.imgChatMessageSend.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                    viewBind.imgMessageCamera.setVisibility(View.GONE)
                    viewBind.imgMessageCamera.animate().scaleX(0f).scaleY(0f).setDuration(200)
                        .setInterpolator(
                            LinearInterpolator()
                        ).start()
                }*/
            }

            var userStoppedTyping = Runnable {
                if (activity.groupChannel.isTyping) activity.groupChannel.endTyping()
            }
        })
    }
}
package com.joinflatshare.ui.chat.details

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import com.debopam.flatshareprogress.DialogCustomProgress
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatDetailsBinding
import com.joinflatshare.chat.ApplicationChatHandler
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_CAMERA
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_GALLERY
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_PICK_IMAGE
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_STORAGE_AUDIO
import com.joinflatshare.ui.chat.ChatBaseActivity
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.ImageHelper
import com.sendbird.android.SendbirdChat.removeChannelHandler
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.BaseMessage
import java.io.File

open class ChatDetailsActivity : ChatBaseActivity() {
    lateinit var viewBind: ActivityChatDetailsBinding
    lateinit var topbarHandler: ChatDetailsTopbarHandler
    lateinit var dataBind: ChatDetailsViewBind
    lateinit var chatDetailsListener: ChatDetailsListener

    @kotlin.jvm.JvmField
    var messageList = ArrayList<BaseMessage>()
    var chatUser: ModelChatUserResponse? = null

    val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_CHAT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        handleBackClick()
        init()
    }

    private fun init() {
        topbarHandler = ChatDetailsTopbarHandler(this, viewBind)
        dataBind = ChatDetailsViewBind(this, viewBind)
        initialiseChat {
            refresh()
            ChatDetailsHandler(this)
        }
    }

    private fun refresh() {
        val channelUrl = intent.getStringExtra("channel")
        sendBirdChannel.getChannelDetail(channelUrl) { channel: GroupChannel ->
            groupChannel = channel
            topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
            dataBind.setData()
            topbarHandler.showChatTopbar(true, true)
            fetchMessages()
            if (messageList.size == 0) {
                chatDetailsListener = ChatDetailsListener(this, viewBind, dataBind)
            }
        }
    }

    private fun fetchMessages() {
        sendBirdMessage.getMessageHistory(groupChannel) { list ->
            messageList.clear()
            if (list.size > 0) messageList.addAll(list)
            if (groupChannel.unreadMessageCount > 0) {
                groupChannel.markAsRead(null)
                Handler(Looper.getMainLooper()).postDelayed({
                    ApplicationChatHandler.getUnreadChannelCount()
                }, 1000)
            }
            if (dataBind.adapter != null) dataBind.adapter.notifyDataSetChanged()
            dataBind.alterChatView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allPermitted = true
        for (result in grantResults) if (result != PackageManager.PERMISSION_GRANTED) {
            allPermitted = false
            break
        }
        if (allPermitted) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> chatDetailsListener.showCameraOption()
                REQUEST_CODE_STORAGE_AUDIO -> {
                    CommonMethod.makeToast("Hold the mic to record, release to send")
                    chatDetailsListener.micListener?.checkPermission()
                }
            }
        } else CommonMethod.makeToast("Permission not granted")
    }

    @Deprecated("To be removed in later version")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY, REQUEST_CODE_CAMERA -> dataBind.mediaBottomSheet.sendGalleryImage(
                    data
                )

                REQUEST_CODE_PICK_IMAGE -> if (data != null) {
                    apiManager.showProgress()
                    val uri = data.data
                    AmazonUploadFile().upload(
                        uri!!.path?.let { File(it) },
                        AmazonUploadFile.AWS_TYPE_FLAT_DP
                    ) { intent: Intent?, requestCode1: Int ->
                        DialogCustomProgress.hideProgress(this);
                        if (requestCode1 == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                            ImageHelper.loadImage(
                                this,
                                R.drawable.ic_flat_chat, viewBind.includeChatTopbar.imgProfile,
                                ImageHelper.getFlatDpWithAws(
                                    sendBirdChannel.getFlatId(groupChannel.url)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun pickImage() {
        ImageHelper.pickImageFromGallery(this, 1f, 1f)
    }

    override fun onDestroy() {
        removeChannelHandler(CHANNEL_HANDLER_ID)
        if (dataBind.timer != null) dataBind.timer?.cancel()
        super.onDestroy()
    }

    private fun handleBackClick() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (dataBind.adapter.selectedMessage != null) {
                        dataBind.adapter.selectedMessage = null
                        topbarHandler.showTopBarIconTwo(0)
                        topbarHandler.showTopBarIconThree(0)
                        topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                        dataBind.adapter.notifyDataSetChanged()
                    } else
                        CommonMethod.finishActivity(this@ChatDetailsActivity)
                }
            })
    }
}
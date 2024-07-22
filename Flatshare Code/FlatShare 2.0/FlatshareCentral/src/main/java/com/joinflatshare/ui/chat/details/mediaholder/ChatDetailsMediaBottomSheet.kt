package com.joinflatshare.ui.chat.details.mediaholder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.debopam.progressdialog.DialogCustomProgress
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.IncludeChatAddmediaBinding
import com.joinflatshare.chat.metadata.MessageMetaData
import com.joinflatshare.chat.metadata.PreviousMessageMetaData
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.interfaces.OnFileFetched
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.ui.base.gpsfetcher.GpsHandlerCallback
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.helper.RealPathUtil
import com.joinflatshare.utils.permission.PermissionUtil
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.FileMessage
import com.sendbird.android.message.UserMessage
import com.sendbird.android.params.FileMessageCreateParams
import com.sendbird.android.params.UserMessageCreateParams
import java.io.File

class ChatDetailsMediaBottomSheet(val activity: ChatDetailsActivity) {
    private var height = 0
    private var layout: IncludeChatAddmediaBinding = activity.viewBind.includeChatMedia
    private var mediaAnimator: ChatDetailsMediaAnimator = ChatDetailsMediaAnimator(this)

    init {
        setListener()
    }

    fun toggleView() {
        if (layout.bottomSheetMedia.visibility == View.GONE) {
            mediaAnimator.show()
            if (height == 0) setHeight()
            activity.viewBind.imgMessageAddmultimedia.setImageResource(R.drawable.arrow_down)
        } else {
            mediaAnimator.hide()
            activity.viewBind.imgMessageAddmultimedia.setImageResource(R.drawable.ic_chat_plus)
        }
    }

    private fun setHeight() {
        if (height == 0) {
            val card = activity.findViewById<MaterialCardView>(R.id.card_media_location)
            card.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    card.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    height = card.width
                    setHeight()
                }
            })
        } else {
            val ll = activity.findViewById<LinearLayout>(R.id.ll_media_holder)
            var i = 0
            while (i < ll.childCount) {
                val card = (ll.getChildAt(i) as LinearLayout).getChildAt(0) as MaterialCardView
                card.layoutParams = LinearLayout.LayoutParams(height, height)
                i += 2
            }
        }
    }

    private fun setListener() {
        val ll = activity.findViewById<LinearLayout>(R.id.ll_media_holder)
        var i = 0
        while (i < ll.childCount) {
            val position = i
            ll.getChildAt(i).setOnClickListener { v: View? ->
                when (position) {
                    0 -> sendLocation()
                    4 -> showGallery()
                    6 -> showContact()
                    else -> CommonMethod.makeToast("Under Development")
                }
                toggleView()
            }
            i += 2
        }
    }

    fun hide() {
        mediaAnimator.hide()
    }

    /*----------------------LOCATION-----------------------*/
    private fun sendLocation() {
        activity.apiManager.showProgress()
        activity.getLocation(activity,object : GpsHandlerCallback {
            override fun onLocationUpdate(latitude: Double, longitude: Double) {
                DialogCustomProgress.hideProgress(activity);
                // Send user location
                val modelMessageData = MessageMetaData()
                modelMessageData.messageType = SendBirdConstants.MESSAGE_TYPE_LOCATION
                modelMessageData.locationLatitude = latitude
                modelMessageData.locationLongitude = longitude
                val mapStaticImageHandler = MapStaticImageHandler(activity)
                mapStaticImageHandler.getMapImage(latitude, longitude, object : OnFileFetched {
                    override fun onFetched(file: File) {
                        val params = FileMessageCreateParams()
                        params.file = file
                        params.fileSize = file.length().toInt()
                        if (activity.dataBind.adapter.selectedMessage != null) {
                            // Reply message
                            params.parentMessageId =
                                activity.dataBind.adapter.selectedMessage.messageId
                            modelMessageData.previousMessageMetaData = handleReplyParentMessage()
                        }
                        params.data = modelMessageData.getJson(modelMessageData)
                        activity.sendBirdMessage.sendFileMessage(
                            activity.groupChannel, params
                        ) { userMessage: FileMessage? ->
                            activity.viewBind.edtChatMessage.setText("")
                            activity.dataBind.replyViewBind.hide()
                            if (activity.dataBind.adapter.selectedMessage != null) {
                                activity.dataBind.adapter.selectedMessage = null
                                activity.topbarHandler.showTopBarIconTwo(0)
                                activity.topbarHandler.showTopBarIconThree(0)
                                activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                            }
                            activity.messageList.add(0, userMessage!!)
                            if (activity.dataBind.adapter != null) activity.dataBind.adapter.notifyItemInserted(
                                0
                            )
                            Handler(Looper.getMainLooper())
                                .postDelayed({
                                    activity.viewBind.rvMessageList.smoothScrollToPosition(
                                        0
                                    )
                                }, 500)
                        }
                    }

                })
            }

            override fun onLocationFailed() {
                DialogCustomProgress.hideProgress(activity);
                CommonMethod.makeToast("Could not proceed without user location")
            }

        })
    }

    /*----------------------Document Picker-----------------------*/
    fun showDocPicker() {
        PermissionUtil.validatePermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (granted) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "file/*"
                        CommonMethod.switchActivity(
                            activity,
                            intent
                        ) { result ->
                            if (result?.resultCode == Activity.RESULT_OK) {
                                sendDocument(result.data)
                            }
                        }
                    }
                }
            })
    }

    private fun sendDocument(data: Intent?) {
        if (data != null) {
            var path = data.dataString
            path = RealPathUtil.getRealPath(activity, data.data)
            //            if (path.contains("/storage"))
//                path = path.substring(path.indexOf("storage"));
            val file = File(path)
            if (file.exists()) {
                val modelMessageData = MessageMetaData()
                modelMessageData.messageType = SendBirdConstants.MESSAGE_TYPE_DOCUMENT
                // Create file params
                val params = FileMessageCreateParams()
                params.file = file
                params.fileSize = file.length().toInt()
                if (activity.dataBind.adapter.selectedMessage != null) {
                    // Reply message
                    params.parentMessageId = activity.dataBind.adapter.selectedMessage.messageId
                    modelMessageData.previousMessageMetaData = handleReplyParentMessage()
                }
                params.data = modelMessageData.getJson(modelMessageData)
                activity.sendBirdMessage.sendFileMessage(
                    activity.groupChannel, params
                ) { userMessage: FileMessage? ->
                    activity.viewBind.edtChatMessage.setText("")
                    activity.dataBind.replyViewBind.hide()
                    if (activity.dataBind.adapter.selectedMessage != null) {
                        activity.dataBind.adapter.selectedMessage = null
                        activity.topbarHandler.showTopBarIconTwo(0)
                        activity.topbarHandler.showTopBarIconThree(0)
                        activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                    }
                    activity.messageList.add(0, userMessage!!)
                    if (activity.dataBind.adapter != null) activity.dataBind.adapter.notifyItemInserted(
                        0
                    )
                }
            }
        }
    }

    /*----------------------Gallery-----------------------*/
    private fun showGallery() {
        PermissionUtil.validatePermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (granted) {
                        ImageHelper.pickImageFromGallery(
                            activity,
                            0f,
                            0f,
                            RequestCodeConstants.REQUEST_CODE_GALLERY
                        )
                    }
                }
            })
    }

    fun sendGalleryImage(data: Intent?) {
        if (data != null) {
            val uri = data.data
            val file = ImageHelper.copyFile(activity, uri)
            activity.apiManager.showProgress()
            val modelMessageData = MessageMetaData()
            modelMessageData.messageType = SendBirdConstants.MESSAGE_TYPE_IMAGE
            // Create file params
            val params = FileMessageCreateParams()
            params.file = file
            params.fileSize = (file!!.length().toInt())
            if (activity.dataBind.adapter.selectedMessage != null) {
                // Reply message
                params.parentMessageId = activity.dataBind.adapter.selectedMessage.messageId
                modelMessageData.previousMessageMetaData = handleReplyParentMessage()
            }
            params.data = modelMessageData.getJson(modelMessageData)
            activity.sendBirdMessage.sendFileMessage(
                activity.groupChannel, params
            ) { userMessage: FileMessage? ->
                DialogCustomProgress.hideProgress(activity);
                activity.viewBind.edtChatMessage.setText("")
                activity.dataBind.replyViewBind.hide()
                if (activity.dataBind.adapter.selectedMessage != null) {
                    activity.dataBind.adapter.selectedMessage = null
                    activity.topbarHandler.showTopBarIconTwo(0)
                    activity.topbarHandler.showTopBarIconThree(0)
                    activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                }
                activity.messageList.add(0, userMessage!!)
                activity.dataBind.adapter.notifyItemInserted(0)
                Handler(Looper.getMainLooper())
                    .postDelayed({
                        activity.viewBind.rvMessageList.smoothScrollToPosition(
                            0
                        )
                    }, 500)
            }
        }
    }

    /*----------------------Contact-----------------------*/
    private fun showContact() {
        PermissionUtil.validatePermission(
            activity,
            Manifest.permission.READ_CONTACTS,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (granted) {
                        val intent =
                            Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                        CommonMethod.switchActivity(
                            activity,
                            intent
                        ) { result ->
                            if (result?.resultCode == Activity.RESULT_OK && result.data != null) {
                                sendContact(result.data)
                            }
                        }
                    }
                }
            })
    }

    fun sendContact(data: Intent?) {
        if (data != null && data.data != null) {
            val contactData = data.data
            val cursor = activity.contentResolver.query(contactData!!, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val hasPhone =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                val contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                var name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number = ""
                if (hasPhone == "1") {
                    val phones = activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null
                    )
                    while (phones!!.moveToNext()) {
                        number =
                            phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                .replace("[-() ]".toRegex(), "")
                    }
                    phones.close()
                }
                cursor.close()
                if (name == null) name = ""
                if (!number.isEmpty()) {
                    var w = ""
                    for (i in 0 until number.length) {
                        if (number[i] != ' ') w += number[i]
                    }
                    number = w
                } else number = ""
                if (!name.isEmpty() && !number.isEmpty()) {
                    val modelMessageData = MessageMetaData()
                    modelMessageData.messageType = SendBirdConstants.MESSAGE_TYPE_CONTACT
                    val params = UserMessageCreateParams()
                    params.message = "$name,$number"
                    if (activity.dataBind.adapter.selectedMessage != null) {
                        // Reply message
                        params.parentMessageId = activity.dataBind.adapter.selectedMessage.messageId
                        modelMessageData.previousMessageMetaData = handleReplyParentMessage()
                    }
                    params.data = modelMessageData.getJson(modelMessageData)
                    activity.sendBirdMessage.sendTextMessage(
                        activity.groupChannel, params
                    ) { userMessage: UserMessage? ->
                        activity.viewBind.edtChatMessage.setText("")
                        activity.dataBind.replyViewBind.hide()
                        if (activity.dataBind.adapter.selectedMessage != null) {
                            activity.dataBind.adapter.selectedMessage = null
                            activity.topbarHandler.showTopBarIconTwo(0)
                            activity.topbarHandler.showTopBarIconThree(0)
                            activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                        }
                        activity.messageList.add(0, userMessage!!)
                        activity.dataBind.adapter.notifyItemInserted(0)
                        Handler(Looper.getMainLooper())
                            .postDelayed({
                                activity.viewBind.rvMessageList.smoothScrollToPosition(
                                    0
                                )
                            }, 500)
                    }
                } else CommonMethod.makeToast("Name or number not found")
            }
        }
    }


    /*----------------------Mic Audio-----------------------*/
    fun sendAudio(path: String?) {
        if (path == null || path.isEmpty()) {
            CommonMethod.makeToast("Failed to send audio")
            return
        }
        val groupFile = File(path)
        if (!groupFile.exists()) {
            CommonMethod.makeToast("Failed to send audio")
            return
        }
        activity.apiManager.showProgress()
        val modelMessageData = MessageMetaData()
        modelMessageData.messageType = SendBirdConstants.MESSAGE_TYPE_AUDIO
        // Create file params
        val params = FileMessageCreateParams().apply {
            file = groupFile
            fileSize = groupFile.length().toInt()
        }
        if (activity.dataBind.adapter.selectedMessage != null) {
            // Reply message
            params.parentMessageId = activity.dataBind.adapter.selectedMessage.messageId
            modelMessageData.previousMessageMetaData = handleReplyParentMessage()
        }
        params.data = modelMessageData.getJson(modelMessageData)
        activity.sendBirdMessage.sendFileMessage(
            activity.groupChannel, params
        ) { userMessage: FileMessage? ->
            DialogCustomProgress.hideProgress(activity);
            activity.viewBind.edtChatMessage.setText("")
            activity.dataBind.replyViewBind.hide()
            if (activity.dataBind.adapter.selectedMessage != null) {
                activity.dataBind.adapter.selectedMessage = null
                activity.topbarHandler.showTopBarIconTwo(0)
                activity.topbarHandler.showTopBarIconThree(0)
                activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
            }
            activity.messageList.add(0, userMessage!!)
            activity.dataBind.adapter.notifyItemInserted(0)
        }
    }

    /*----------------------Reply Parent Message-----------------------*/
    fun handleReplyParentMessage(): PreviousMessageMetaData {
        val previousMessageMetaData = PreviousMessageMetaData()
        val selectedMessage: BaseMessage = activity.dataBind.adapter.selectedMessage
        val metaData = Gson().fromJson(
            selectedMessage.data,
            MessageMetaData::class.java
        )
        previousMessageMetaData.messageId = selectedMessage.messageId
        previousMessageMetaData.messageType = metaData.messageType
        previousMessageMetaData.senderId = selectedMessage.sender!!.userId
        if (selectedMessage is UserMessage) {
            previousMessageMetaData.message = selectedMessage.message
        } else if (selectedMessage is FileMessage) {
            previousMessageMetaData.message = selectedMessage.url
        }
        return previousMessageMetaData
    }
}
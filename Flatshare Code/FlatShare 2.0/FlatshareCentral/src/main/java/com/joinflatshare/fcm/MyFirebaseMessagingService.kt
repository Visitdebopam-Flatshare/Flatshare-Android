package com.joinflatshare.fcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.chat.metadata.MessageMetaData
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.constants.NotificationConstants
import com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_SENDBIRD
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.sendbird.android.SendbirdChat
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        if (!user?.id.isNullOrBlank()) {
            val intent =
                Intent(this@MyFirebaseMessagingService, ExploreActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            var title: String? = ""
            var body: String? = ""

            if (remoteMessage.data.containsKey("sendbird")) {
                // Sendbird Push Notifications
                val sendbird = JSONObject(remoteMessage.data["sendbird"]!!)
                CommonMethod.makeLog("Sendbird", sendbird.toString())
                val channel = sendbird.get("channel") as JSONObject
                var channelUrl = channel["channel_url"] as String
                val unreadCount = channel["channel_unread_message_count"] as Int
                intent.putExtra("channel", channelUrl)
                intent.putExtra("type", NOTIFICATION_TYPE_SENDBIRD)

                when (sendbird.optString("type")) {
                    "FILE" -> {
                        val files = sendbird.optJSONArray("files")
                        if (files != null && !files.isNull(0)) {
                            val dataObject = files[0] as JSONObject
                            val metaDataString = dataObject.optString("data")
                            val metaData =
                                Gson().fromJson(metaDataString, MessageMetaData::class.java)
                            body = when (metaData.messageType) {
                                SendBirdConstants.MESSAGE_TYPE_LOCATION -> "Sent location"
                                SendBirdConstants.MESSAGE_TYPE_CONTACT -> "Sent a contact"
                                SendBirdConstants.MESSAGE_TYPE_IMAGE -> "Sent an image"
                                else -> "Sent aa file"
                            }
                        } else body = "Sent a file"
                    }

                    "MESG",
                    "ADMM" -> {
                        body = sendbird.optString("message")
                    }
                }

                when (unreadCount) {
                    1 -> {
                        title = "You have a new message"
                    }

                    else -> {
                        title = "You have $unreadCount new messages"
                    }
                }
                if (channelUrl.startsWith("USER_")) {
                    channelUrl = channelUrl.replace("USER_", "")
                    val users = channelUrl.split("_")
                    if (users.size == 2) {
                        val currentUser = SendbirdChat.currentUser?.userId
                        var otherUser = ""
                        if (users[0] == currentUser)
                            otherUser = users[1]
                        else otherUser = users[0]
                        SendBirdApiManager().getUserDetails(otherUser,
                            OnResponseCallback { response ->
                                val otherName = response?.nickname
                                if (!TextUtils.isEmpty(otherName))
                                    title += " from $otherName"
                                createNotification(title, body, intent)
                                return@OnResponseCallback
                            })
                    }
                }
                createNotification(title, body, intent)


            } else {
                // App Push notifications
                if (remoteMessage.notification != null) {
                    title = remoteMessage.notification?.title
                    body = remoteMessage.notification?.body

                    intent.putExtra("title", title)
                    intent.putExtra("body", body)
                    // check for data
                    val data = remoteMessage.data
                    if (data.containsKey("type")) {
                        CommonMethod.makeLog("Notification Type", data["type"])
                        intent.putExtra("type", data["type"])
                        sendBroadcastForRequestReload(this, data["type"]!!)
                    }
                    if (data.containsKey("flatId"))
                        intent.putExtra("flatId", data["flatId"])
                    if (data.containsKey("userId"))
                        intent.putExtra("userId", data["userId"])
                    createNotification(title, body, intent)
                }
            }

        }
    }

    private fun createNotification(title: String?, body: String?, intent: Intent) {
        if (!title.isNullOrBlank() && !body.isNullOrBlank()) {
            CommonMethod.makeLog("Notification Title", title)
            CommonMethod.makeLog("Notification Body", body)
            intent.putExtra("notification", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pIntent =
                PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                            or PendingIntent.FLAG_IMMUTABLE
                )
            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(
                pIntent, title, body
            )
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.FIREBASE_TOKEN, token)
        FlatShareApplication.getDbInstance().userDao().insert(UserDao.USER_NEED_FCM_UPDATE, "1")
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_FIREBASE_TOKEN_UPDATED))
    }


    private fun sendBroadcastForRequestReload(context: Context, type: String) {
        val intent = Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_NOTIFICATION)
        when (type) {
            NotificationConstants.NOTIFICATION_TYPE_CONNECTION_F2U ->
                intent.putExtra("requestType", ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U)

            NotificationConstants.NOTIFICATION_TYPE_CONNECTION_U2F ->
                intent.putExtra("requestType", ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F)

            NotificationConstants.NOTIFICATION_TYPE_CONNECTION_FHT,
            NotificationConstants.NOTIFICATION_TYPE_CHAT_FHT_U2U ->
                intent.putExtra("requestType", ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT)

            NotificationConstants.NOTIFICAITON_TYPE_DATING_CASUAL ->
                intent.putExtra(
                    "requestType",
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                )

            NotificationConstants.NOTIFICAITON_TYPE_DATING_LONG_TERM ->
                intent.putExtra(
                    "requestType",
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                )

            NotificationConstants.NOTIFICAITON_TYPE_DATING_ACTIVITY_PARTNERS ->
                intent.putExtra(
                    "requestType",
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                )

            NotificationConstants.NOTIFICATION_TYPE_INVITE_FLAT_MEMBER ->
                intent.putExtra("requestType", ChatRequestConstants.FLAT_REQUEST_CONSTANT)

        }
        if (intent.hasExtra("requestType"))
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    companion object {
        @SuppressLint("UseCompatLoadingForDrawables")
        private fun NotificationManager.sendNotification(
            intent: PendingIntent,
            title: String?,
            body: String?,
        ) {
            val inboxStyle = NotificationCompat.InboxStyle()
            val instance = FlatShareApplication.instance

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = instance.getString(R.string.app_name)
                val descriptionText = instance.getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(
                    instance.getString(R.string.default_notification_channel_id),
                    name, importance
                ).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                createNotificationChannel(channel)
            }

            val bitmapDrawable: BitmapDrawable =
                instance.resources.getDrawable(R.mipmap.ic_logo_notification) as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            // Build the notification
            val builder = NotificationCompat.Builder(
                instance,
                instance.getString(R.string.default_notification_channel_id)
            )
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(intent)
                .setSmallIcon(R.mipmap.ic_logo_notification)
                .setColor(ContextCompat.getColor(instance, R.color.blue_dark))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                )

            builder.setStyle(inboxStyle)

            builder.setSmallIcon(R.mipmap.ic_logo_notification);
            builder.color = ContextCompat.getColor(
                instance,
                R.color.color_text_secondary
            )
            // Generate notification ID
            val rand = kotlin.random.Random.nextInt()
            // Deliver the notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(
                        instance,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    notify(
                        instance.resources.getString(R.string.app_name),
                        rand,
                        builder.build()
                    )
            } else
                notify(instance.resources.getString(R.string.app_name), rand, builder.build())
        }

        fun generateTestNotification() {
            if (AppConstants.isAppLive)
                return
            val notificationManager = ContextCompat.getSystemService(
                FlatShareApplication.instance,
                NotificationManager::class.java
            ) as NotificationManager
            val intent =
                Intent(FlatShareApplication.instance, ExploreActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            val pIntent =
                PendingIntent.getActivity(
                    FlatShareApplication.instance, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                            or PendingIntent.FLAG_IMMUTABLE
                )
            notificationManager.sendNotification(
                pIntent, "Test Title", "Test Body"
            )
        }
    }

}
package com.joinflatshare.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityNotificationsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.pojo.notification.NotificationItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class NotificationActivity : BaseActivity() {
    lateinit var viewBind: ActivityNotificationsBinding
    lateinit var dataBinder: NotificationDataBinder
    val notifications = ArrayList<NotificationItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Notifications", 0, 0)
        showBottomMenu(this)
        dataBinder = NotificationDataBinder(this, viewBind)
        fetchData()
        NotificationListener(this, viewBind)
        MixpanelUtils.onScreenOpened("Notification")
    }

    fun fetchData() {
        dataBinder.currentPage = 0
        dataBinder.getNotifications()
    }

    override fun onResume() {
        super.onResume()
        AppConstants.menuSelected = 2
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastListener,
            IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_USER_REQUESTS_UPDATED)
        )
        baseViewBinder.applyMenuClick()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastListener)
    }

    private val broadcastListener = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            dataBinder.bindFriendRequests()
        }

    }
}
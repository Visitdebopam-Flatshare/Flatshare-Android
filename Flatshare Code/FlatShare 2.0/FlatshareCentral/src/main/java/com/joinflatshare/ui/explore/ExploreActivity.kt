package com.joinflatshare.ui.explore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.fcm.FcmNavigationUtils
import com.joinflatshare.fcm.MyFirebaseMessagingService
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.preferences.PreferenceActivity
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ExploreActivity : BaseActivity() {
    lateinit var viewBind: ActivityExploreBinding
    lateinit var binder: ExploreBinder
    var apiController: ExploreApiController? = null

    val userData = ArrayList<com.joinflatshare.pojo.explore.UserRecommendationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        init()
        moveToPreference()
        handleDeepLinking()
//        MutualContactHandler.scheduleContactHandler(this)
    }

    private fun init() {
        apiController = ExploreApiController(this, viewBind)
        binder = ExploreBinder(this)
        ExploreListener(this, viewBind)
    }

    private fun moveToPreference() {
        if (intent.getBooleanExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE, false)) {
            intent.removeExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE)
            val intent = Intent(this, PreferenceActivity::class.java)
            intent.putExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE, true)
            CommonMethod.switchActivity(this, intent, false)
        } else {
            reloadFeed()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            profileImageReloadListener,
            IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_PROFILE_IMAGE)
        )
        AppConstants.menuSelected = 0
        baseViewBinder.applyMenuClick()
        if (AppConstants.isFeedReloadRequired)
            reloadFeed()
    }

    private fun reloadFeed() {
        MixpanelUtils.onScreenOpened("Feed")
        viewBind.txtNoFeed.text = ""
        if (apiController == null) {
            init()
            return
        }
        apiController?.currentPage = 0
        AppConstants.loggedInUser = FlatShareApplication.getDbInstance().userDao().getUser()
        binder.hideAll()
        binder.setup()
    }

    private fun handleDeepLinking() {
        if (intent.getBooleanExtra(IntentConstants.INTENT_DEEPLINK, false)) {
            intent.removeExtra(IntentConstants.INTENT_DEEPLINK)
            DeepLinkHandler.handle(this, intent)
        } else
            handleFcmNavigation()
    }

    private fun handleFcmNavigation() {
        if (intent.getBooleanExtra(IntentConstants.INTENT_NOTIFICATION, false)) {
            intent.removeExtra(IntentConstants.INTENT_NOTIFICATION)
            FcmNavigationUtils(this, intent)
        } //else MyFirebaseMessagingService.generateTestNotification()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(profileImageReloadListener)
        super.onDestroy()
    }

    private val profileImageReloadListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_PROFILE_IMAGE -> {
                    baseViewBinder.img_menu_profile.setImageResource(R.drawable.ic_user)
                    ImageHelper.loadImage(
                        this@ExploreActivity, R.drawable.ic_user,
                        baseViewBinder.img_menu_profile,
                        ImageHelper.getProfileImageWithAwsFromPath(AppConstants.loggedInUser?.dp)
                    )
                }
            }
        }

    }
}
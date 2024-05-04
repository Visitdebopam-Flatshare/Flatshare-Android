package com.joinflatshare.ui.explore

import android.app.Activity
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
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.fcm.FcmNavigationUtils
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.services.MutualContactHandler
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.notifications.RequestHandler
import com.joinflatshare.ui.preferences.flat.PreferenceActivity
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ExploreActivity : BaseActivity() {
    lateinit var viewBind: ActivityExploreBinding
    lateinit var binder: ExploreBinder
    var apiController: ExploreApiController? = null

    @Deprecated("Removed")
    var flat: MyFlatData? = null
    val userData = ArrayList<UserRecommendationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        moveToPreference()
        handleDeepLinking()
//        reloadRequests()
        init()
//        checkUserLocation()
        MutualContactHandler.scheduleContactHandler(this)
    }

    private fun init() {
        apiController = ExploreApiController(this, viewBind)
        binder = ExploreBinder(this)
        ExploreListener(this, viewBind)
        reloadFeed()
    }

    private fun moveToPreference() {
        if (intent.getBooleanExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE, false)) {
            intent.removeExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE)
            val intent = Intent(this, PreferenceActivity::class.java)
            CommonMethod.switchActivity(
                this,
                intent
            ) { result ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    AppConstants.isFeedReloadRequired = true
                    reloadFeed()
                }
            }
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

    fun reloadFeed() {
        MixpanelUtils.onScreenOpened("Feed")
        viewBind.txtNoFeed.text = ""
        if (apiController == null) {
            init()
            return
        }
        apiController?.currentPage = 0
        AppConstants.loggedInUser = FlatShareApplication.getDbInstance().userDao().getUser()
        binder.hideAll()
        /*if (FlatShareApplication.getDbInstance().userDao().getFlatResponse() == null) {
            baseApiController.getMyFlat { intent: Intent?, requestCode: Int ->
                run {
                    flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                    binder.setup()
                    if (buttonBinder == null) buttonBinder = ProfileButtonBinder(this, viewBind)
                    buttonBinder?.setAdapter()
                }
            }
        } else {
            flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
            binder.setup()
        }*/
        revertAllUsersToFHT()
    }

    private fun revertAllUsersToFHT() {
        if (AppConstants.loggedInUser?.isFHTSearch?.value == false) {
            val user = AppConstants.loggedInUser
            user?.isDateSearch?.value = false
            user?.isFlatSearch?.value = false
            user?.isFHTSearch?.value = true
            baseApiController.updateUser(true, user, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    binder.setup()
                }
            })
        } else binder.setup()
    }

    private fun reloadRequests() {
        FlatShareApplication.getDbInstance().userDao().insert(UserDao.USER_REQUEST_API_PENDING, 1)
        RequestHandler.getFriendRequest(this, true)
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
        }
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
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
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.fcm.FcmNavigationUtils
import com.joinflatshare.pojo.explore.FlatRecommendationItem
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.services.MutualContactHandler
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.notifications.RequestHandler
import com.joinflatshare.ui.profile.myprofile.ProfileButtonBinder
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ExploreActivity : BaseActivity() {
    lateinit var viewBind: ActivityExploreBinding
    lateinit var binder: ExploreBinder
    var buttonBinder: ProfileButtonBinder? = null
    var apiController: ExploreApiController? = null
    val flatData = ArrayList<FlatRecommendationItem>()
    val userData = ArrayList<UserRecommendationItem>()
    var flat: MyFlatData? = null
    var errorMessage = ""
    var SEARCH_TYPE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        handleDeepLinking()
        reloadRequests()
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

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            profileImageReloadListener,
            IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_PROFILE_IMAGE)
        )
        AppConstants.menuSelected = 0
        baseViewBinder.applyMenuClick()
        /*if (hasMovedToSettings) {
            hasMovedToSettings = false
            checkUserLocation()
        }*/
        if (AppConstants.isFeedReloadRequired)
            reloadFeed()
        /*if (buttonBinder == null) buttonBinder = ProfileButtonBinder(this, viewBind)
        buttonBinder?.setAdapter()*/
    }

    fun reloadFeed() {
        MixpanelUtils.onScreenOpened("Feed")
        viewBind.txtFlatSearch.text = ""
        if (apiController == null) {
            init()
            return
        }
        apiController?.currentPage = 0
        apiController?.currentFHTPage = 0
        AppConstants.loggedInUser = FlatShareApplication.getDbInstance().userDao().getUser()
        binder.hideAll()
        ExploreAdapter.shouldShowPaymentGateway = true
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
            apiManager.updateProfile(true, user, object : OnResponseCallback<Any?> {
                override fun oncallBack(response: Any?) {
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
        if (intent.getBooleanExtra("deepLink", false)) {
            intent.removeExtra("deepLink")
            DeepLinkHandler.handle(this, intent)
        } else
            handleFcmNavigation()
    }

    private fun handleFcmNavigation() {
        if (intent.getBooleanExtra("notification", false)) {
            intent.removeExtra("notification")
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
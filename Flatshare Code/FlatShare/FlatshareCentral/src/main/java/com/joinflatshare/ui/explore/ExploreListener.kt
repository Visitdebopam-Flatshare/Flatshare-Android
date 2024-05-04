package com.joinflatshare.ui.explore

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.pojo.user.DateProperties
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.base.gpsfetcher.GpsHandlerCallback
import com.joinflatshare.ui.date.DatePreferenceActivity
import com.joinflatshare.ui.flat.flat_profile.MyFlatActivity
import com.joinflatshare.ui.preferences.flat.PrefFlatActivity
import com.joinflatshare.ui.preferences.flatmate.PrefFlatmateActivity
import com.joinflatshare.ui.profile.myprofile.ProfileButtonBinder
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils.onButtonClicked
import com.joinflatshare.utils.permission.PermissionUtil

class ExploreListener(
    private val activity: ExploreActivity,
    private val viewBind: ActivityExploreBinding
) : View.OnClickListener {

    init {
        viewBind.llExploreSharedFlatOff.setOnClickListener(this)
        viewBind.llExploreFlatOff.setOnClickListener(this)
        viewBind.llExploreFhtOff.setOnClickListener(this)
        viewBind.llExploreDateOff.setOnClickListener(this)
        viewBind.btnEditpreferences.setOnClickListener(this)
        viewBind.cardPreferences.setOnClickListener(this)
        viewBind.cardExploreOpenSearch.setOnClickListener(this)
        viewBind.cardExploreFhtSearch.setOnClickListener(this)
        viewBind.cardExploreDateSearch.setOnClickListener(this)
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.reloadFeed()
            viewBind.pullToRefresh.isRefreshing = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.cardPreferences.id -> {
                val intent = Intent(activity, PrefFlatActivity::class.java)
                intent.putExtra("isFhtView", true)
                CommonMethod.switchActivity(
                    activity,
                    intent
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        val isFhtView = result.data?.getBooleanExtra("isFHT", false)
                        if (isFhtView != null && isFhtView)
                            activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
                        else activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT

                        AppConstants.isFeedReloadRequired = true
                    }
                }
                /*val intent: Intent
                if (AppConstants.loggedInUser?.isDateSearch?.value == true && activity.SEARCH_TYPE == BaseActivity.TYPE_DATE) {
                    intent = Intent(activity, DatePreferenceActivity::class.java)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK) {
                            AppConstants.isFeedReloadRequired = true
                        }
                    }
                } else if (AppConstants.loggedInUser?.isFlatSearch?.value == true
                    || AppConstants.loggedInUser?.isFHTSearch?.value == true
                ) {
                    intent = Intent(activity, PrefFlatActivity::class.java)
                    val isFhtView = activity.SEARCH_TYPE.equals(BaseActivity.TYPE_FHT)
                    intent.putExtra("isFhtView", isFhtView)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK) {
                            val isFhtView = result.data?.getBooleanExtra("isFHT", false)
                            if (isFhtView != null && isFhtView)
                                activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
                            else activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT

                            AppConstants.isFeedReloadRequired = true
                        }
                    }
                } else {
                    intent = Intent(activity, PrefFlatmateActivity::class.java)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK) {
                            AppConstants.isFeedReloadRequired = true
                        }
                    }
                }*/
            }

            viewBind.btnEditpreferences.id -> {
                viewBind.cardPreferences.performClick()
            }

            viewBind.cardExploreFhtSearch.id -> {
                if (activity.SEARCH_TYPE != BaseActivity.TYPE_FHT) {
                    activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
                    activity.binder.setup()
                    activity.buttonBinder?.setAdapter()
                    activity.viewBind.rvExploreButtons.scrollToPosition(1)
                }
            }

            viewBind.cardExploreOpenSearch.id -> {
                if (!(activity.SEARCH_TYPE == BaseActivity.TYPE_USER
                            || activity.SEARCH_TYPE == BaseActivity.TYPE_FLAT)
                ) {
                    activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT
                    activity.binder.setup()
                    activity.buttonBinder?.setAdapter()
                    activity.viewBind.rvExploreButtons.scrollToPosition(0)
                }
            }

            viewBind.cardExploreDateSearch.id -> {
                if (activity.SEARCH_TYPE != BaseActivity.TYPE_DATE) {
                    activity.SEARCH_TYPE = BaseActivity.TYPE_DATE
                    activity.binder.setup()
                    activity.buttonBinder?.setAdapter()
                    activity.viewBind.rvExploreButtons.scrollToPosition(2)
                }
            }

            viewBind.llExploreSharedFlatOff.id -> {
                AppConstants.loggedInUser?.isFlatSearch?.value = true
                activity.apiManager.updateProfile(
                    true, AppConstants.loggedInUser
                ) {
                    CommonMethods.makeToast(
                        "Your flat search is turned on"
                    )
                    onButtonClicked("Shared Flat Search")
                    if (activity.buttonBinder == null) activity.buttonBinder =
                        ProfileButtonBinder(activity, viewBind)
                    activity.buttonBinder?.setAdapter()
                    activity.reloadFeed()
                }
            }

            viewBind.llExploreFhtOff.id -> {
                AppConstants.loggedInUser?.isFHTSearch?.value = true
                activity.apiManager.updateProfile(
                    true, AppConstants.loggedInUser
                ) {
                    CommonMethods.makeToast(
                        "Flathunt Together is turned on"
                    )
                    onButtonClicked("Flathunt Together Search")
                    /*if (activity.buttonBinder == null) activity.buttonBinder =
                        ProfileButtonBinder(activity, viewBind)
                    activity.buttonBinder?.setAdapter()*/
                    activity.reloadFeed()
                }
            }

            viewBind.llExploreFlatOff.id -> {
                val intent = Intent(activity, MyFlatActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.llExploreDateOff.id -> {
                checkLocationPermission()
            }
        }
    }

    private fun checkLocationPermission() {
        if (!activity.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showLocationMessage()
        } else fetchLocation()
    }

    private fun takeLocationPermission() {
        PermissionUtil.validatePermission(
            activity,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (!granted) {
                        showLocationMessage()
                    } else fetchLocation()
                }

            })
    }

    private fun showLocationMessage() {
        AlertDialog.showAlert(
            activity,
            "",
            "flatshare needs your current location to look for potential matches around you.",
            "Allow",
            "Cancel"
        ) { _, requestCode ->
            if (requestCode == 1)
                takeLocationPermission()
        }
    }

    private fun fetchLocation() {
        activity.getLocation(activity,object : GpsHandlerCallback {
            override fun onLocationUpdate(latitude: Double, longitude: Double) {
                val user = AppConstants.loggedInUser
                user?.isDateSearch?.value = true
                if (user?.dateProperties == null)
                    user?.dateProperties = DateProperties()
                user?.location?.loc?.coordinates?.clear()
                user?.location?.loc?.coordinates?.add(longitude)
                user?.location?.loc?.coordinates?.add(latitude)
                activity.apiManager.updateProfile(
                    true, user
                ) {
                    CommonMethods.makeToast(
                        "Vibe Check turned on"
                    )
                    onButtonClicked("Vibe Check Search")
                    if (activity.buttonBinder == null) activity.buttonBinder =
                        ProfileButtonBinder(activity, viewBind)
                    activity.buttonBinder?.setAdapter()
                    activity.reloadFeed()
                }
            }

            override fun onLocationFailed() {
                CommonMethods.makeToast("Could not proceed without user location")
            }
        })
    }
}
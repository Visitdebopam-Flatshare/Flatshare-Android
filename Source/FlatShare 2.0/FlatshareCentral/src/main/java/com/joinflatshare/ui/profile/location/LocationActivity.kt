package com.joinflatshare.ui.profile.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileLocationBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentConstants
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.base.gpsfetcher.GpsHandlerCallback
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.permission.PermissionManager

/**
 * Created by debopam on 08/03/24
 */
class LocationActivity : BaseActivity() {
    private lateinit var viewBind: ActivityProfileLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileLocationBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
    }

    private fun init() {
        viewBind.btnBack.setOnClickListener {
            CommonMethod.finishActivity(this)
        }
        viewBind.btnSkip.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            intent.putExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE, true)
            CommonMethod.switchActivity(this, intent, false)
            finishAffinity()
        }
        viewBind.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    val intent = PermissionManager.getPermissionIntent(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        RequestCodeConstants.REQUEST_CODE_LOCATION
                    )
                    activityLauncher.launch(
                        intent,
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK) {
                            getLocation(this@LocationActivity, object : GpsHandlerCallback {
                                override fun onLocationUpdate(latitude: Double, longitude: Double) {
                                    val model = ModelLocation()
                                    model.loc.coordinates.add(longitude)
                                    model.loc.coordinates.add(latitude)
                                    val user = AppConstants.loggedInUser
                                    user?.location = model
                                    baseApiController.updateUser(true, user, object :
                                        OnUserFetched {
                                        override fun userFetched(resp: UserResponse?) {
                                            intent.setClass(
                                                this@LocationActivity,
                                                ExploreActivity::class.java
                                            )
                                            intent.putExtra(
                                                IntentConstants.INTENT_MOVE_TO_PREFERENCE,
                                                true
                                            )
                                            CommonMethod.switchActivity(
                                                this@LocationActivity,
                                                intent,
                                                false
                                            )
                                            finishAffinity()
                                        }

                                    })
                                }

                                override fun onLocationFailed() {
                                    CommonMethod.makeToast("Could not retrieve user location")
                                }

                            })
                        }
                    }
                }
            }
        }
    }
}
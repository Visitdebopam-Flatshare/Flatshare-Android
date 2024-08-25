package com.joinflatshare.ui.profile.details

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 27/05/24
 */
class ProfileDetailsActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityProfileDetailsBinding
    private var dataBinder: ProfileDetailsDataBinder? = null
    protected var listener: ProfileDetailsListener? = null
    var image: ArrayList<String> = ArrayList()
    internal var userResponse: UserResponse? = null
    var apiController: ProfileDetailsApiController? = null
    internal var user: User? = null
    internal var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        init()
        MixpanelUtils.onScreenOpened("View Profile")
    }

    private fun init() {
        dataBinder = ProfileDetailsDataBinder(this, viewBinding)
        userId = intent.getStringExtra("phone")
        apiController = ProfileDetailsApiController(this, viewBinding)
        apiController?.getProfile()
    }

    internal fun initUserData(response: UserResponse?) {
        this.userResponse = response
        this.user = userResponse!!.data
        val fname = user?.name?.firstName
        val lname = user?.name?.lastName
        var name = "$fname $lname"
        if (name.length > 25) {
            name = name.substring(0, 25) + "..."
        }

        showTopBar(this, true, name, 0, 0)
        dataBinder?.setData()
        listener = ProfileDetailsListener(this, viewBinding)
    }
}
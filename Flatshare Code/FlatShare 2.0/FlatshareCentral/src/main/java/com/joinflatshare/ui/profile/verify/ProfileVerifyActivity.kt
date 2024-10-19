package com.joinflatshare.ui.profile.verify

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileVerifyBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 27/05/24
 */
class ProfileVerifyActivity : BaseActivity() {
    lateinit var viewBind: ActivityProfileVerifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileVerifyBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        MixpanelUtils.onScreenOpened("Profile Verify")
        init()
        bind()
    }

    private fun bind() {
        if (!AppConstants.isAppLive)
            viewBind.edtVerify.setText("807985062506")
    }

    private fun init() {
        ProfileVerifyListener(this, viewBind)
    }
}
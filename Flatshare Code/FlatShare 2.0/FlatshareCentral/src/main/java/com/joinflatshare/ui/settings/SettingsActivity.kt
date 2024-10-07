package com.joinflatshare.ui.settings

import android.os.Bundle
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivitySettingsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.mixpanel.MixpanelUtils.onButtonClicked

/**
 * Created by debopam on 21/05/24
 */
class SettingsActivity : BaseActivity() {
    private lateinit var viewBind: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        MixpanelUtils.onScreenOpened("Settings")
        showTopBar(this, true, "Settings", 0, 0)
        SettingsViewBind(viewBind)
        SettingsListener(this, viewBind)
        checkAdminFeatures()
    }

    private fun checkAdminFeatures() {
        if (!AppConstants.isAppLive && AppConstants.isAdmin)
            viewBind.cardProfileAdmin.visibility = View.VISIBLE
    }
}
package com.joinflatshare.ui.settings

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivitySettingsBinding
import com.joinflatshare.ui.base.BaseActivity
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
        showTopBar(this, true, "Settings", 0, 0)
        SettingsViewBind(viewBind)
        SettingsListener(this, viewBind)
        onButtonClicked("Settings")
    }
}
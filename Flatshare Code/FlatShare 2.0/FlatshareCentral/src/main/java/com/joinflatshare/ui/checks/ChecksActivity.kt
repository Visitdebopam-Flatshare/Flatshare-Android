package com.joinflatshare.ui.checks

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityCheckListBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ChecksActivity : BaseActivity() {
    lateinit var viewBind: ActivityCheckListBinding
    lateinit var dataBinder: ChecksDataBinder
    var mode = MODE_SUPER_CHECKS
    var source = SOURCE_RECEIVED

    internal companion object {
        const val MODE_SUPER_CHECKS = "0"
        const val MODE_CHECKS = "1"
        const val SOURCE_RECEIVED = "0"
        const val SOURCE_SENT = "1"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityCheckListBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "", 0, 0)
        showBottomMenu(this)
        dataBinder = ChecksDataBinder(this, viewBind)
        fetchData()
        ChecksListener(this, viewBind)
        MixpanelUtils.onScreenOpened("Checks")
    }


    fun fetchData() {
        dataBinder.list.clear()
        dataBinder.currentPage = 0
        dataBinder.isDataFetching = false
        dataBinder.hasMoreData = true
        dataBinder.getRequests()
    }

    override fun onResume() {
        super.onResume()
        AppConstants.menuSelected = 2
        baseViewBinder.applyMenuClick()
    }
}
package com.joinflatshare.ui.connection

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityConnectionListBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ConnectionListActivity : BaseActivity() {
    lateinit var viewBind: ActivityConnectionListBinding
    lateinit var dataBinder: ConnectionDataBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityConnectionListBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        dataBinder = ConnectionDataBinder(this, viewBind)
        fetchData()
        MixpanelUtils.onScreenOpened("Connections")
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
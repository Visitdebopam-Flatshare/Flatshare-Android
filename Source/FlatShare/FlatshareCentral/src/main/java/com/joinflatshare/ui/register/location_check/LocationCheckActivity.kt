package com.joinflatshare.ui.register.location_check

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.databinding.ActivityLocationCheckBinding
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.ui.register.RegisterBaseActivity

class LocationCheckActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityLocationCheckBinding
    lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityLocationCheckBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        apiManager = ApiManager(this)
        click()
    }

    private fun click() {
        viewBind.llLocationBack.setOnClickListener {
            finishAffinity()
        }
        viewBind.btnCheckLocation.setOnClickListener {
            if (viewBind.btnCheckLocation.text == "Close flatshare") finishAffinity()
        }
    }

    override fun onBackPressed() {

    }
}
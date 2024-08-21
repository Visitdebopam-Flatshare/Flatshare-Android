package com.joinflatshare.ui.register.about

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileAboutBinding
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDealBinding
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.register.RegisterBaseActivity

class RegisterAboutActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityProfileAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileAboutBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
        RegisterAboutListener(this, viewBind)
    }

    private fun init() {
        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        if (response?.data?.allowedSkips?.isSkippingAboutAllowed == false) {
            viewBind.btnSkip.visibility = View.INVISIBLE
        }

        if (TextUtils.equals(AppConstants.loggedInUser?.profession, "Student")) {
            viewBind.edtProfileWork.visibility = View.GONE
        }
    }

    internal fun workLimit() {
        var due: Int = 150 - viewBind.edtProfileWork.getText().toString().length
        viewBind.txtProfileWorkLimit.text = "" + due
    }
}
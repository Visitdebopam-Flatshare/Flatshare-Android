package com.joinflatshare.ui.register.about

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterAboutBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.ui.register.RegisterBaseActivity

class RegisterAboutActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityRegisterAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRegisterAboutBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "5")
        init()
        RegisterAboutListener(this, viewBind)
    }

    private fun init() {
        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        if (response?.data?.allowedSkips?.isSkippingAboutAllowed == false) {
            viewBind.btnSkip.visibility = View.INVISIBLE
        }

        if (TextUtils.equals(AppConstants.loggedInUser?.profession, "Student")) {
            viewBind.llRegisterAboutWork.visibility = View.GONE
            viewBind.txtRegisterAboutDesc.text =
                "Tell us a bit about your college and hometown to help us find you your perfect flatmate."
        }
    }

    internal fun workLimit() {
        val due: Int = 150 - viewBind.edtProfileWork.getText().toString().length
        viewBind.txtProfileWorkLimit.text = "" + due
    }
}
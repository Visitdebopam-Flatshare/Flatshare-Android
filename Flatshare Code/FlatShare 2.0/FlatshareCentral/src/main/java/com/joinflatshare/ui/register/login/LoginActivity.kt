package com.joinflatshare.ui.register.login

import android.os.Bundle
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityLoginBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 03/02/24
 */
class LoginActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
        bind()
    }

    private fun bind() {
        if (!AppConstants.isAppLive)
//            viewBind.edtLogin.setText("9832394089")
            viewBind.edtLogin.setText("8169533929")
//            viewBind.edtLogin.setText("6303546278")
//            viewBind.edtLogin.setText("9263471358")
    }

    private fun init() {
        LoginListener(this, viewBind)
        CommonMethod.showHtmlInTextView(
            viewBind.txtLoginTerms, resources.getString(
                R.string.login_terms,
                "By tapping Next, you acknowledge that you have read and understood the ",
                "Flatshare Terms",
                " and ",
                "Privacy Policy",
                ", and you agree to be bound by them."
            )
        )
    }
}
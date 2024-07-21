package com.joinflatshare.ui.register.otp

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityOtpBinding
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

/**
 * Created by debopam on 03/02/24
 */
class OtpListener(
    private val activity: OtpActivity,
    private val viewBind: ActivityOtpBinding
) : OnClickListener {
    init {
        viewBind.btnOtp.setOnClickListener(this)
        viewBind.txtotpResend.setOnClickListener(this)
        viewBind.btnBack.setOnClickListener(this)
        setOtpListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnOtp.id -> {
                val otp = viewBind.edtOtp.text.toString().trim()
                if (otp.length != 6)
                    return
                if (activity.intent.getBooleanExtra("aadhar", false))
                    activity.apiController?.verifyAadhaar(otp)
                else if (activity.intent.getBooleanExtra("delete", false))
                    activity.apiController?.deleteAccount(otp)
                else
                    activity.apiController?.login(otp)
            }

            viewBind.txtotpResend.id -> {
                activity.showError(false, "We have sent a new OTP to your phone.")
            }

            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }
        }
    }

    private fun setOtpListener() {
        viewBind.edtOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                val number = edit.toString().trim()
                if (number.length == 6)
                    viewBind.btnOtp.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_blue
                        )
                    )
                else viewBind.btnOtp.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.drawable_button_light_blue
                    )
                )
            }

        })
    }
}
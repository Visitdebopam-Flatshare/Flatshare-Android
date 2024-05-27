package com.joinflatshare.ui.profile.verify

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileVerifyBinding
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.user.AdhaarRequest
import com.joinflatshare.ui.register.otp.OtpActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 03/02/24
 */
internal class ProfileVerifyListener(
    private val activity: ProfileVerifyActivity,
    private val viewBind: ActivityProfileVerifyBinding
) : OnClickListener {
    init {
        viewBind.btnVerify.setOnClickListener(this)
        setPhoneListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnVerify.id -> {
                val number = viewBind.edtVerify.text.toString().trim()
                if (number.length != 12)
                    return
                WebserviceManager().sendAadharOtp(
                    activity,
                    AdhaarRequest(number),
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            val resp = Gson().fromJson(response, com.joinflatshare.pojo.BaseResponse::class.java)
                            if (resp.status == 200) {
                                MixpanelUtils.onButtonClicked("12digit Aadhar Continue")
                                val intent = Intent(activity, OtpActivity::class.java)
                                intent.putExtra("phone", number)
                                intent.putExtra("aadhar", true)
                                CommonMethod.switchActivity(
                                    activity,
                                    intent
                                ) { result ->
                                    if (result?.resultCode == Activity.RESULT_OK) {
                                        activity.setResult(Activity.RESULT_OK, intent)
                                        CommonMethod.finishActivity(activity)
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun setPhoneListener() {
        viewBind.edtVerify.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                val number = edit.toString().trim()
                if (number.length == 10)
                    viewBind.btnVerify.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_blue
                        )
                    )
                else viewBind.btnVerify.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.drawable_button_light_blue
                    )
                )
            }

        })
    }

}
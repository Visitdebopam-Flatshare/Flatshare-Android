package com.joinflatshare.ui.register.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityOtpBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.helper.CommonMethod.makeLog
import java.util.regex.Pattern

/**
 * Created by debopam on 03/02/24
 */
class OtpActivity : RegisterBaseActivity() {
    private lateinit var viewBind: ActivityOtpBinding
    lateinit var phone: String
    var apiController: OtpApiController? = null
    lateinit var smsReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        phone = intent.getStringExtra("phone")!!
        init()
    }

    private fun init() {
        OtpListener(this, viewBind)
        apiController = OtpApiController(this)
        customiseScreen()
        initReceiver()
        if (AppConstants.isAppLive) {
            apiController!!.sendOtp()
        } else {
            viewBind.edtOtp.setText("236854")
        }
    }

    private fun customiseScreen() {
        if (intent.getBooleanExtra("aadhar", false)) {

        } else if (intent.getBooleanExtra("delete", false)) {
            viewBind.txtHeader.text = "Verify the deletion code sent to your phone"
            viewBind.btnOtp.text = "Delete Account"
        }
    }

    internal fun showError(isError: Boolean, text: String) {
        viewBind.txtotpError.setTextColor(
            ContextCompat.getColor(
                this,
                if (isError) R.color.red else R.color.green
            )
        )
        viewBind.txtotpError.text = text
    }

    fun initReceiver() {
        smsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getBooleanExtra("valid", false)) {
                    val msgBody = intent.getStringExtra("message")
                    makeLog("Message Broadcast", msgBody)
                    val pattern = Pattern.compile("(|^)\\d{6}")
                    if (msgBody != null) {
                        val matcher = pattern.matcher(msgBody)
                        if (matcher.find()) {
                            val otp = matcher.group(0)
                            makeLog("OTP", otp)
                            viewBind.edtOtp.setText(otp)
                        }
                    }
                }
            }
        }
    }


}
package com.joinflatshare.ui.register.login

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityLoginBinding
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.interfaces.OnitemClick
import com.joinflatshare.ui.register.otp.OtpActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.ConfigManager

/**
 * Created by debopam on 03/02/24
 */
class LoginListener(
    private val activity: LoginActivity,
    private val viewBind: ActivityLoginBinding
) : OnClickListener {
    init {
        viewBind.txtLoginTerms.setOnClickListener(this)
        viewBind.btnLogin.setOnClickListener(this)
        setPhoneListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.txtLoginTerms.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Terms & Conditions"))
                list.add(ModelBottomSheet(0, "Privacy"))
                BottomSheetView(activity, list
                ) { _, position ->
                    when (position) {
                        0 -> {
                            activity.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.joinflatshare.com/terms-and-condition")
                                )
                            )
                        }

                        1 -> {
                            activity.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.joinflatshare.com/privacy-policy")
                                )
                            )
                        }
                    }
                }
            }

            viewBind.btnLogin.id -> {
                val number = viewBind.edtLogin.text.toString().trim()
                if (number.length != 10)
                    return
                val configManager = ConfigManager(activity)
                if (configManager.isUserBlocked(viewBind.edtLogin.text.toString().trim())) {
                    AlertDialog.showAlert(
                        activity,
                        "Your account is restricted. If this looks like a mistake, please reach out to us at\nhello@flatshare.club.",
                        "OK"
                    )
                } else {
                    val intent = Intent(activity, OtpActivity::class.java)
                    intent.putExtra("phone", number)
                    CommonMethod.switchActivity(activity, intent, false)
                    MixpanelUtils.numberEntered(number)
                }
            }
        }
    }

    private fun setPhoneListener() {
        viewBind.edtLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                val number = edit.toString().trim()
                if (number.length == 10)
                    viewBind.btnLogin.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_blue
                        )
                    )
                else viewBind.btnLogin.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.drawable_button_light_blue
                    )
                )
            }

        })
    }

}
package com.joinflatshare.ui.profile.myprofile

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.children
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.AdhaarOtp
import com.joinflatshare.pojo.user.AdhaarRequest
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 07/03/23
 */
class ProfileVerifyHandler(
    private val activity: ProfileActivity,
    mainViewBind: ActivityProfileBinding
) {
    private val edtListAdhaarNo = ArrayList<EditText>()
    private val edtListAdhaarOtp = ArrayList<EditText>()
    private val viewBind = mainViewBind.includeProfileVerify

    init {
        // initialization
        for (view in viewBind.llAadharNoEdt.children) {
            if (view is EditText) {
                edtListAdhaarNo.add(view)
            }
        }

        for (view in viewBind.llAadharOtpEdt.children) {
            if (view is EditText) {
                edtListAdhaarOtp.add(view)
            }
        }
        setListener()
        click()
    }

    fun prepare() {
        viewBind.llAadharNo.visibility = View.VISIBLE
        viewBind.llAadharOtp.visibility = View.GONE
        if (!AppConstants.loggedInUser?.location?.name.isNullOrBlank()) {
            if (AppConstants.loggedInUser?.location?.name != "Bangalore")
                viewBind.txtProfileVerifyHeader.text = "Get Verified"
        }
    }

    private fun setListener() {
        // Delete code
        for (i in edtListAdhaarNo.indices) {
            edtListAdhaarNo[i].setOnKeyListener { _: View?, i1: Int, keyEvent: KeyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && i1 == KeyEvent.KEYCODE_DEL) {
                    edtListAdhaarNo[i].setText("")
                    if (i > 0) {
                        edtListAdhaarNo[i - 1].requestFocus()
                        edtListAdhaarNo[i - 1].append("")
                    }
                }
                false
            }
        }
        for (i in edtListAdhaarOtp.indices) {
            edtListAdhaarOtp[i].setOnKeyListener { view: View?, i1: Int, keyEvent: KeyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && i1 == KeyEvent.KEYCODE_DEL) {
                    edtListAdhaarNo[i].setText("")
                    if (i > 0) {
                        edtListAdhaarOtp[i - 1].requestFocus()
                        edtListAdhaarOtp[i - 1].append("")
                    }
                }
                false
            }
        }

        // Append Code
        for (i in edtListAdhaarNo.indices) {
            edtListAdhaarNo[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    if (i < 11) {
                        if (edtListAdhaarNo[i].length() > 0) edtListAdhaarNo[i + 1].requestFocus()
                    } else {
                        if (edtListAdhaarNo[i].length() > 0) {
                            CommonMethods.hideSoftKeyboard(activity)
                        }
                    }
                }
            })
        }
        for (i in edtListAdhaarOtp.indices) {
            edtListAdhaarOtp[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    if (i < 5) {
                        if (edtListAdhaarOtp[i].length() > 0) edtListAdhaarOtp[i + 1].requestFocus()
                    } else {
                        if (edtListAdhaarOtp[i].length() > 0) {
                            CommonMethods.hideSoftKeyboard(activity)
                        }
                    }
                }
            })
        }
    }

    private fun click() {
        viewBind.btnProfileVerifyContinue.setOnClickListener {
            verifyAdhaar()
        }
        viewBind.btnProfileVerifySubmit.setOnClickListener {
            verifyAdhaarOTP()
        }
    }

    private fun verifyAdhaar() {
        var adhaarNo = ""
        for (edt in edtListAdhaarNo) {
            adhaarNo += edt.text.toString()
        }
        if (adhaarNo.length != 12)
            CommonMethod.makeToast("Please enter a valid Aadhaar Number")
        else {
            activity.apiManager.verifiyAdhaar(
                AdhaarRequest(adhaarNo)
            ) { response ->
                val resp = response as com.joinflatshare.pojo.BaseResponse
                if (resp.status == 200) {
                    MixpanelUtils.onButtonClicked("12digit Aadhar Continue")
                    viewBind.llAadharNo.visibility = View.GONE
                    viewBind.llAadharOtp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun verifyAdhaarOTP() {
        var adhaarOTP = ""
        for (edt in edtListAdhaarOtp) {
            adhaarOTP += edt.text.toString()
        }
        if (adhaarOTP.length != 6)
            CommonMethod.makeToast("Please enter a valid OTP")
        else {
            activity.apiManager.verifiyAdhaarOtp(
                AdhaarOtp(adhaarOTP)
            ) { response ->
                val resp = response as com.joinflatshare.pojo.BaseResponse
                if (resp.status == 200) {
                    MixpanelUtils.onButtonClicked("Aadhar OTP Submit")
                    getProfile()
                }
            }
        }
    }

    private fun getProfile() {
        activity.baseApiController.getUser(true,AppConstants.loggedInUser?.id,object :OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                val sendBirdUser = SendBirdUser(activity)
                val params = HashMap<String, String>()
                params["nickname"] =
                    AppConstants.loggedInUser?.name?.firstName + " " + AppConstants.loggedInUser?.name?.lastName
                params["profile_url"] =
                    if (AppConstants.loggedInUser?.dp.isNullOrBlank()) "" else AppConstants.loggedInUser?.dp!!
                sendBirdUser.updateUser(
                    params
                ) { }
                viewBind.imgProfileVerifyClose.performClick()
                CommonMethod.makeToast("Congrats! You're Verified.")
                DialogLottieViewer(activity, R.raw.lottie_verify_profile, null)
                activity.dataBinder.setData()
            }
        })
    }
}
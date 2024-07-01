package com.joinflatshare.ui.register.create

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding
import com.joinflatshare.chat.ApplicationChatHandler
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.fcm.NotificationPermissionHandler
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.ui.register.photo.RegisterPhotoActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.DeviceInformationCollector
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 03/02/24
 */
class ProfileCreateActivity : RegisterBaseActivity() {
    private lateinit var viewBind: ActivityProfileCreateBinding
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileCreateBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        if (user == null) {
            CommonMethod.logout(this)
            return
        }
        setFilter()
        ProfileCreateListener(this, viewBind)
    }


    private fun setFilter() {
        val filtertxt = arrayOfNulls<InputFilter>(1)
        filtertxt[0] =
            InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
                if (end > start) {
                    val acceptedChars = charArrayOf(
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '
                    )
                    for (index in start until end) {
                        if (!String(acceptedChars).contains(source[index].toString())) {
                            return@InputFilter if (source.length == 0) "" else source.subSequence(
                                0,
                                source.length - 1
                            )
                        }
                    }
                }
                null
            }
        viewBind.edtFname.filters = arrayOf(filtertxt[0])
        viewBind.edtLname.filters = arrayOf(filtertxt[0])
    }

    fun updateUser(modelUser: User?) {
        // Update Profile
        WebserviceManager().updateProfile(true, this, modelUser,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: UserResponse? = Gson().fromJson(response, UserResponse::class.java)
                    CommonMethods.registerUser(resp)
                    callAfterRegister(resp?.data!!)
                }
            })
    }

    private fun callAfterRegister(modelUser: User) {
        // Collect Device Information
        DeviceInformationCollector()

        //Register in Sendbird
        if (AppConstants.isSendbirdLive) {
            ApplicationChatHandler().initialise { text: String ->
                if (text == "1") {
                    val sendBirdUser = SendBirdUser(this@ProfileCreateActivity)
                    val params =
                        HashMap<String, String>()
                    params["user_id"] = modelUser.id
                    params["nickname"] =
                        modelUser.name!!.firstName + " " + modelUser.name!!.lastName
                    params["profile_url"] = ""
                    sendBirdUser.createUser(
                        params
                    ) { }
                }
            }
        }

        // Requesting Notification permission for Android 13
        NotificationPermissionHandler(this).showNotificationPermission {
            MixpanelUtils.sendToMixPanel("Registration Complete")
            val intent = Intent(this, RegisterPhotoActivity::class.java)
            CommonMethod.switchActivity(this, intent, false)
            finishAffinity()
        }
    }
}
package com.joinflatshare.ui.register.create

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding
import com.joinflatshare.chat.ApplicationChatHandler
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.fcm.NotificationPermissionHandler
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.ui.register.photo.RegisterPhotoActivity
import com.joinflatshare.ui.settings.SettingsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.DeviceInformationCollector
import com.joinflatshare.webservice.api.WebserviceManager

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
        MixpanelUtils.onScreenOpened("Onboarding Registration")
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        if (user == null) {
            CommonMethod.logout(this)
            return
        }
        setFilter()
        ProfileCreateListener(this, viewBind)
    }

    private fun setFilter() {
        val filterText = arrayOfNulls<InputFilter>(1)
        filterText[0] =
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
        viewBind.edtFname.filters = arrayOf(filterText[0])
        viewBind.edtLname.filters = arrayOf(filterText[0])
    }

    fun updateUser(modelUser: User?) {
        updateUser(modelUser, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                CommonMethods.registerUser(resp)
                callAfterRegister(modelUser!!)
            }
        })
    }

    private fun callAfterRegister(modelUser: User) {
        // Collect Device Information
        DeviceInformationCollector()

        CommonMethod.sendUserToDB(modelUser)

        // Requesting Notification permission for Android 13
        NotificationPermissionHandler(this).showNotificationPermission {
            MixpanelUtils.sendToMixPanel("Registration Complete")
            val intent = Intent(this, RegisterPhotoActivity::class.java)
            CommonMethod.switchActivity(this, intent, false)
            finishAffinity()
        }

        //Register in Sendbird
        /*WebserviceManager.uiWebserviceHandler.showProgress(this)
        ApplicationChatHandler().initialise { text: String ->
            WebserviceManager.uiWebserviceHandler.hideProgress(this)
            if (text == "1") {
                registerSendbird(modelUser)
                // Requesting Notification permission for Android 13
                NotificationPermissionHandler(this).showNotificationPermission {
                    MixpanelUtils.sendToMixPanel("Registration Complete")
                    val intent = Intent(this, RegisterPhotoActivity::class.java)
                    CommonMethod.switchActivity(this, intent, false)
                    finishAffinity()
                }
            }
        }*/
    }

    private fun registerSendbird(modelUser: User) {
        val userName = modelUser.name!!.firstName + " " + modelUser.name!!.lastName
        CommonMethod.makeLog("SendBird User Register", userName + "  " + modelUser.id)
        val sendBirdUser = SendBirdUser(this@ProfileCreateActivity)
        val params =
            HashMap<String, String>()
        params["user_id"] = modelUser.id
        params["nickname"] = userName
        params["profile_url"] = ""
        sendBirdUser.createUser(
            params
        ) {
            // Update the User again since nickname is not registering
            params.remove("user_id")
            params.remove("profile_url")
            params["nickname"] = userName
            sendBirdUser.updateUser(
                user?.id,
                params
            ) { response: ModelChatUserResponse? -> }
        }
    }
}
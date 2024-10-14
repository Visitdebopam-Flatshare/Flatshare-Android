package com.joinflatshare.ui.register.photo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.debopam.ImagePicker
import com.debopam.ImagePicker.Companion.getError
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterPhotoBinding
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import java.io.File

/**
 * Created by debopam on 04/02/24
 */
class RegisterPhotoActivity : BaseActivity() {
    private lateinit var viewBind: ActivityRegisterPhotoBinding
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRegisterPhotoBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        MixpanelUtils.onScreenOpened("Onboarding Photo")
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "1")
        init()
    }

    private fun init() {
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        RegisterPhotoListener(this, viewBind)
        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        if (response?.data?.allowedSkips?.isSkippingProfilePictureAllowed == false) {
            viewBind.btnSkip.visibility = View.INVISIBLE
        }
    }

    fun pickImage(fromGallery: Boolean) {
        if (fromGallery) ImageHelper.pickImageFromGallery(this, imageResult, 1f, 1f)
        else ImageHelper.pickImageFromCamera(this, imageResult, 1f, 1f)
    }

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data:Intent? = result.data
                //Image Uri will not be null for RESULT_OK
                if (data != null) {
                    DialogCustomProgress.showProgress(this)
                    val uri = data.data
                    val ap = AmazonUploadFile()
                    ap.upload(
                        File(uri?.path), AmazonUploadFile.AWS_TYPE_PROFILE_IMAGE
                    ) { intent: Intent?, requestCode1: Int ->
                        DialogCustomProgress.hideProgress(this)
                        if (requestCode1 == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                            val serverPath = intent?.getStringExtra("localpath")
                            deleteOldProfileImage()
                            user?.dp = serverPath
                            baseApiController.updateUser(true, user, object : OnUserFetched {
                                override fun userFetched(resp: UserResponse?) {
                                    viewBind.imgPhoto.setImageURI(uri)
                                    updateSendbirdUserProfile()
                                    CommonMethod.makeToast("Profile picture updated")
                                    MixpanelUtils.sendToMixPanel("Photo Uploaded")
                                    viewBind.btnUploadPhoto.text = "Next"
                                    viewBind.btnSkip.text = "Change Photo"
                                    viewBind.txtHeader.text = "Set as profile photo"
                                    viewBind.btnSkip.visibility = View.VISIBLE
                                }

                            })
                        } else {
                            Logger.log(
                                "Failed to update profile picture", Logger.LOG_TYPE_ERROR
                            )
                            CommonMethod.makeToast("Failed to update profile picture")
                        }
                    }
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                CommonMethod.makeToast(getError(result.data))
            }

        }


    private fun updateSendbirdUserProfile() {
        val sendBirdUser = SendBirdUser(this)
        val params: HashMap<String, String> = HashMap()
        params["profile_url"] = if (user?.dp.isNullOrEmpty()) "" else user?.dp!!
        sendBirdUser.updateUser(
            user?.id,
            params
        ) { response: ModelChatUserResponse? -> }
    }

    private fun deleteOldProfileImage() {
        val oldDp = user?.dp
        if (!oldDp.isNullOrEmpty()) AmazonDeleteFile().delete(oldDp, null)
    }
}
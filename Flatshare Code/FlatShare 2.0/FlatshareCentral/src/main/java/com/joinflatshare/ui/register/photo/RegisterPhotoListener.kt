package com.joinflatshare.ui.register.photo

import android.Manifest
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterPhotoBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.ui.profile.language.LanguageActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.permission.PermissionUtil

/**
 * Created by debopam on 04/02/24
 */
class RegisterPhotoListener(
    private val activity: RegisterPhotoActivity,
    private val viewBind: ActivityRegisterPhotoBinding
) : OnClickListener {

    init {
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnUploadPhoto.setOnClickListener(this)
        viewBind.btnSkip.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.btnUploadPhoto.id -> {
                val text = viewBind.btnUploadPhoto.text.toString()
                when (text) {
                    "Upload Photo" -> {
                        takePhoto()
                    }

                    "Next" -> {
                        val intent = Intent(activity, LanguageActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }

            viewBind.btnSkip.id -> {
                val text = viewBind.btnSkip.text.toString()
                when (text) {
                    "Change Photo" -> {
                        viewBind.imgPhoto.visibility = View.VISIBLE
                        viewBind.txtPhoto.visibility = View.GONE
                        takePhoto()
                        val response =
                            FlatShareApplication.getDbInstance().appDao().getConfigResponse()
                        if (response?.data?.allowedSkips?.isSkippingProfilePictureAllowed == true) {
                            viewBind.btnSkip.text = "Skip for now"
                        }
                    }

                    "Skip for now" -> {
                        viewBind.imgPhoto.visibility = View.GONE
                        viewBind.txtPhoto.visibility = View.VISIBLE
                        val fname = AppConstants.loggedInUser?.name?.firstName
                        val lname = AppConstants.loggedInUser?.name?.lastName
                        if (!fname.isNullOrEmpty() && fname.length > 1
                            && !lname.isNullOrEmpty() && lname.length > 1
                        ) {
                            viewBind.txtPhoto.text = "" + fname[0] + lname[0]
                        } else {
                            viewBind.txtPhoto.text = "NA"
                        }
                        viewBind.btnUploadPhoto.text = "Next"
                        viewBind.btnSkip.text = "Change Photo"
                        viewBind.txtHeader.text = "Set as profile photo"
                    }
                }
            }
        }
    }

    private fun takePhoto() {
        val list = ArrayList<ModelBottomSheet>()
        list.add(ModelBottomSheet(0, "Choose from gallery"))
        list.add(ModelBottomSheet(0, "Take Photo"))
        BottomSheetView(activity, list) { _, position ->
            when (position) {
                0 -> {
                    PermissionUtil.validatePermission(
                        activity, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) { granted: Boolean ->
                        if (granted) activity.pickImage(true) else {
                            val message =
                                "Storage permission not provided for uploading image"
                            CommonMethod.makeToast(message)
                            Logger.log(
                                message,
                                Logger.LOG_TYPE_PERMISSION
                            )
                        }
                    }
                }

                1 -> {
                    PermissionUtil.validatePermission(
                        activity, Manifest.permission.CAMERA
                    ) { granted: Boolean ->
                        if (granted) activity.pickImage(false) else {
                            val message =
                                "Camera permission not provided for uploading image"
                            CommonMethod.makeToast(message)
                            Logger.log(
                                message,
                                Logger.LOG_TYPE_PERMISSION
                            )
                        }
                    }
                }
            }
        }
    }
}
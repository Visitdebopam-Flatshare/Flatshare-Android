package com.joinflatshare.ui.flat.verify

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatVerifyBinding
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.register.location_check.LocationCheckHandler
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.permission.PermissionUtil
import com.joinflatshare.utils.system.ThemeUtils
import java.io.File


class FlatVerifyListener(
    private val activity: FlatVerifyActivity,
    private val viewBind: ActivityFlatVerifyBinding
) {

    init {
        viewBind.cardVideoHolder.setOnClickListener {
            val myFlatData = FlatShareApplication.getDbInstance().userDao().getFlatData()!!
            if (myFlatData.completed < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                AlertDialog.showAlert(activity, "Your flat profile is not complete yet!")
                return@setOnClickListener
            }
            MixpanelUtils.onButtonClicked("Upload Flat Video")
            PermissionUtil.validatePermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                object : OnPermissionCallback {
                    override fun onCallback(granted: Boolean) {
                        if (granted) {
                            UwMediaPicker
                                .with(activity)                        // Activity or Fragment
                                .setGalleryMode(UwMediaPicker.GalleryMode.VideoGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                                .setGridColumnCount(3)                                  // Grid column count, default is 3
                                .setMaxSelectableMediaCount(1)                         // Maximum selectable media count, default is null which means infinite
                                .setLightStatusBar(ThemeUtils.getTheme(activity) == AppCompatDelegate.MODE_NIGHT_NO)                                // Is llight status bar enable, default is true
                                .setCancelCallback {
                                    run {

                                    }
                                }                    // Will be called when user cancels media selection
                                .launch { selectedMediaList ->
                                    run {
                                        if (selectedMediaList != null) {
                                            if (selectedMediaList.isNotEmpty()) {
                                                activity.videoPath = selectedMediaList[0].mediaPath
                                                CommonMethod.makeLog("Path", activity.videoPath)
                                                viewBind.videoFlat.visibility = View.VISIBLE
                                                viewBind.viewBg.visibility = View.GONE
                                                viewBind.imgUploadIcon.visibility = View.GONE
                                                viewBind.videoFlat.setVideoURI(Uri.parse(activity.videoPath))
                                                viewBind.videoFlat.setOnPreparedListener { mp ->
                                                    if (checkVideoDimension(mp)) {
                                                        mp?.isLooping = true
                                                        viewBind.btnVideoDelete.visibility =
                                                            View.VISIBLE
                                                        viewBind.videoFlat.start()
                                                    } else {
                                                        AlertDialog.showAlert(
                                                            activity,
                                                            "The video should be taken horizontally",
                                                            "Ok"
                                                        ) { intent, requestCode -> viewBind.cardVideoHolder.performClick() }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                    }

                })
        }

        viewBind.btnVerifyProfile.setOnClickListener {
            if (activity.videoPath.isBlank()) {
                CommonMethod.makeToast( "Please select a video")
            } else {
                LocationCheckHandler(
                    activity,
                    AppConstants.loggedInUser?.id,
                    true
                ) { text ->
                    if (text.equals("1")) {
                        activity.apiManager.showProgress()
                        val au = AmazonUploadFile()
                        val file = File(activity.videoPath)
                        if (file.exists()) {
                            MixpanelUtils.onButtonClicked("Post Flat Verification Video")
                            au.upload(
                                file,
                                AmazonUploadFile.AWS_TYPE_FLAT_VIDEO
                            ) { _, requestCode ->
                                DialogCustomProgress.hideProgress(activity);
                                if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                                    activity.isFileUploaded = true
                                    updateFlat()
                                } else CommonMethod.makeToast(
                                    "Failed to upload video"
                                )
                            }
                        } else CommonMethod.makeToast( "The video does not exist")
                    }
                }
            }
        }
        viewBind.btnVideoDelete.setOnClickListener {
            if (activity.isFileUploaded) {
                AlertDialog.showAlert(
                    activity,
                    "",
                    "Do you want to delete this video?",
                    "Delete",
                    "Cancel"
                ) { _, requestCode ->
                    if (requestCode == 1) {
                        deleteVideo()
                    }
                }
            } else {
                clearVideoView()
            }

        }
    }

    private fun clearVideoView() {
        activity.videoPath = ""
        viewBind.videoFlat.visibility = View.GONE
        viewBind.viewBg.visibility = View.VISIBLE
        viewBind.imgUploadIcon.visibility = View.VISIBLE
        viewBind.videoFlat.stopPlayback()
        viewBind.btnVideoDelete.visibility = View.GONE
        viewBind.videoFlat.setVideoURI(null)
    }

    private fun deleteVideo() {
        activity.apiManager.showProgress()
        val id = activity.flat?.mongoId
        val url = "${UrlConstants.USER_IMAGE_URL}$id/Verification.mp4"
        AmazonDeleteFile().delete(url, object : OnUiEventClick {
            override fun onClick(intent: Intent?, requestCode: Int) {
                DialogCustomProgress.hideProgress(activity);
                if (requestCode == AmazonDeleteFile.REQUEST_CODE_SUCCESS) {
                    activity.isFileUploaded = false
                    clearVideoView()
                } else CommonMethod.makeToast( "Failed to delete video")
            }
        })
    }

    private fun checkVideoDimension(mp: MediaPlayer): Boolean {
        val width = mp.videoWidth
        val height = mp.videoHeight
        return width > height
    }

    private fun updateFlat() {
        val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
        flat?.verifier?.id = AppConstants.loggedInUser?.id!!
        flat?.verifier?.coordinates = AppConstants.loggedInUser?.location?.loc?.coordinates!!
        flat?.verifier?.timestamp = DateUtils.getServerDate()
        var distance = DistanceCalculator.calculateDistance(
            AppConstants.loggedInUser?.location?.loc?.coordinates!![0],
            AppConstants.loggedInUser?.location?.loc?.coordinates!![1],
            flat?.flatProperties?.location?.loc?.coordinates!![0],
            flat.flatProperties.location?.loc?.coordinates!![1]
        )
        if (distance.equals("NA"))
            flat.verifier.distance = 0.0
        else {
            distance = distance.replace(" km", "")
            flat.verifier.distance = distance.toDouble()
        }
        activity.apiManager.updateFlat(true, flat, object : OnResponseCallback<Any?> {
            override fun oncallBack(response: Any?) {
                CommonMethod.makeToast( "Video uploaded")
                CommonMethod.finishActivity(activity)
            }
        })
    }
}
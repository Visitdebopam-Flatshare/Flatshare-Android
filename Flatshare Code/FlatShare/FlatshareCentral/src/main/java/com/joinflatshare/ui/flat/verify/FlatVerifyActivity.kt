package com.joinflatshare.ui.flat.verify

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatVerifyBinding
import com.joinflatshare.constants.UrlConstants.USER_IMAGE_URL
import com.joinflatshare.constants.UrlConstants.VIDEO_URL
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.amazonaws.AmazonFileChecker
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.helper.CommonMethod

class FlatVerifyActivity : BaseActivity() {
    lateinit var viewBind: ActivityFlatVerifyBinding
    var videoPath = ""
    var isFileUploaded = false
    val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityFlatVerifyBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Get Flat Verified", 0, 0)
        init()
        FlatVerifyListener(this, viewBind)
        checkExistingVideo()
    }

    private fun init() {
        viewBind.txtFlatName.text =
            "Post your flat video and get ${flat?.name?.trim()} verified!"
        viewBind.cardVideoHolder.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewBind.cardVideoHolder.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = viewBind.cardVideoHolder.width
                viewBind.cardVideoHolder.layoutParams.height = (width / 16) * 9
                viewBind.cardVideoHolder.requestLayout()
            }
        })
    }

    private fun checkExistingVideo() {
        val id = flat?.mongoId
        val url = "$USER_IMAGE_URL$id/Verification.mp4"
        AmazonFileChecker(this).checkObject(
            url
        ) { intent, requestCode ->
            if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                isFileUploaded = true
                viewBind.videoFlat.visibility = View.VISIBLE
                viewBind.btnVideoDelete.visibility = View.VISIBLE
                viewBind.viewBg.visibility = View.GONE
                viewBind.imgUploadIcon.visibility = View.GONE
                val path = "$VIDEO_URL$id/Verification.mp4"
                CommonMethod.makeLog("Path", path)
                viewBind.videoFlat.setVideoPath(path)
                viewBind.videoFlat.start()
            } else {
                isFileUploaded = false
                viewBind.btnVideoDelete.visibility = View.GONE
            }
        }
    }

    override fun onStop() {
        if (viewBind.videoFlat.visibility == View.VISIBLE) {
            viewBind.videoFlat.stopPlayback();
            viewBind.videoFlat.clearAnimation();
            viewBind.videoFlat.suspend(); // clears media player
            viewBind.videoFlat.setVideoURI(null);
        }
        super.onStop()
    }
}
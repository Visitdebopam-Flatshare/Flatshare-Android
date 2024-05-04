package com.joinflatshare.ui.chat.details.audioview

import android.media.MediaRecorder
import android.os.Environment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import java.io.File
import java.io.IOException

class ChatDetailsMicViewBind(private val activity: ChatDetailsActivity) {
    var animBlink: Animation? = null
    var animJump: Animation? = null
    var animJumpFast: Animation? = null
    private lateinit var AudioSavePathInDevice:String
    private var mediaRecorder: MediaRecorder? = null

    init {
        animBlink = AnimationUtils.loadAnimation(activity, R.anim.blink)
        animJump = AnimationUtils.loadAnimation(activity, R.anim.jump)
        animJumpFast = AnimationUtils.loadAnimation(activity, R.anim.jump_fast)
    }

    fun recordMedia() {
        AudioSavePathInDevice = Environment.getExternalStorageDirectory().absolutePath + "/" +
                activity.resources.getString(R.string.app_name) + "_AudioRecording.3gp"
        deleteAudioFile()
        MediaRecorderReady()
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopMediaRecord(): String {
        mediaRecorder?.stop()
        return AudioSavePathInDevice
    }

    fun deleteAudioFile() {
        val file = File(AudioSavePathInDevice)
        if (file.exists()) file.delete()
    }

    private fun MediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setOutputFile(AudioSavePathInDevice)
    }
}
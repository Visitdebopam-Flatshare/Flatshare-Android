package com.joinflatshare.ui.chat.details.audioview

import android.Manifest
import android.animation.Animator
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethods
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask

class ChatDetailsMicListener(private val activity: ChatDetailsActivity) {
    private val micViewBind: ChatDetailsMicViewBind = ChatDetailsMicViewBind(activity)

    // Variables
    private var isDeleting = false
    private var stopTrackingAction = false
    private var handler: Handler? = null
    private var lastX = 0f
    private var lastY: Float = 0f
    private var firstX = 0f
    private var firstY: Float = 0f

    private val directionOffset = 0f
    private var cancelOffset: Float = 0f
    private var lockOffset: Float = 0f
    private var dp = 0f
    private var isLocked = false
    private var isLayoutDirectionRightToLeft: Boolean = false

    private var audioTotalTime = 0
    private var timerTask: TimerTask? = null
    private var audioTimer: Timer? = null
    private var timeFormatter: SimpleDateFormat? = null

    private var userBehaviour = UserBehaviour.NONE

    init {
        handler = Handler(Looper.getMainLooper())
        dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            activity.viewBind.drawerLayout.context.resources.displayMetrics
        )
        timeFormatter = SimpleDateFormat("m:ss", Locale.getDefault())
        checkPermission()
    }

    fun checkPermission() {
        if (activity.hasPermission(Manifest.permission.RECORD_AUDIO) &&
            activity.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            setListener()
            activity.viewBind.imgChatMic.setOnClickListener(null)
        } else {
            activity.viewBind.imgChatMic.setOnTouchListener(null)
            setClick()
        }
    }

    private fun setClick() {
        activity.viewBind.imgChatMic.setOnClickListener { v ->
            activity.requestPermissionsSafely(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                RequestCodeConstants.REQUEST_CODE_STORAGE_AUDIO
            )
        }
    }

    private fun setListener() {
        activity.viewBind.imgChatMic.setOnTouchListener { v, motionEvent ->
            if (motionEvent.getAction() === MotionEvent.ACTION_DOWN) {
                cancelOffset = (CommonMethods.getScreenWidth() / 2.8).toFloat()
                lockOffset = (CommonMethods.getScreenWidth() / 2.5).toFloat()
                if (firstX == 0f) {
                    firstX = motionEvent.getRawX()
                }
                if (firstY == 0f) {
                    firstY = motionEvent.getRawY()
                }
                startRecord()
            } else if (motionEvent.getAction() === MotionEvent.ACTION_UP
                || motionEvent.getAction() === MotionEvent.ACTION_CANCEL
            ) {
                if (motionEvent.getAction() === MotionEvent.ACTION_UP) {
                    stopRecording(RecordingBehaviour.RELEASED)
                }
            } else if (motionEvent.getAction() === MotionEvent.ACTION_MOVE) {
                if (stopTrackingAction) {
                    return@setOnTouchListener true
                }
                var direction = UserBehaviour.NONE
                val motionX: Float = Math.abs(firstX - motionEvent.getRawX())
                val motionY: Float = Math.abs(firstY - motionEvent.getRawY())
                if (if (isLayoutDirectionRightToLeft) motionX > directionOffset && lastX > firstX && lastY > firstY else motionX > directionOffset && lastX < firstX && lastY < firstY) {
                    if (if (isLayoutDirectionRightToLeft) motionX > motionY && lastX > firstX else motionX > motionY && lastX < firstX) {
                        direction = UserBehaviour.CANCELING
                    } else if (motionY > motionX && lastY < firstY) {
                        direction = UserBehaviour.LOCKING
                    }
                } else if (if (isLayoutDirectionRightToLeft) motionX > motionY && motionX > directionOffset && lastX > firstX else motionX > motionY && motionX > directionOffset && lastX < firstX) {
                    direction = UserBehaviour.CANCELING
                } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                    direction = UserBehaviour.LOCKING
                }
                if (direction == UserBehaviour.CANCELING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawY() + activity.viewBind.imgChatMic.getWidth() / 2 > firstY) {
                        userBehaviour = UserBehaviour.CANCELING
                    }
                    if (userBehaviour == UserBehaviour.CANCELING) {
                        translateX(-(firstX - motionEvent.getRawX()))
                    }
                } else if (direction == UserBehaviour.LOCKING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawX() + activity.viewBind.imgChatMic.getWidth() / 2 > firstX) {
                        userBehaviour = UserBehaviour.LOCKING
                    }
                    if (userBehaviour == UserBehaviour.LOCKING) {
                        translateY(-(firstY - motionEvent.getRawY()))
                    }
                }
                lastX = motionEvent.getRawX()
                lastY = motionEvent.getRawY()
            }
            v.onTouchEvent(motionEvent)
            true
        }
    }

    private fun translateY(y: Float) {
        if (y < -lockOffset) {
            locked()
            activity.viewBind.imgChatMic.setTranslationY(0f)
            return
        }
        if (activity.viewBind.layoutLock.visibility == View.GONE) {
            activity.viewBind.layoutLock.setVisibility(View.VISIBLE)
        }
        activity.viewBind.imgChatMic.setTranslationY(y)
        activity.viewBind.layoutLock.setTranslationY(y / 2)
        activity.viewBind.imgChatMic.translationX = 0f
    }

    private fun translateX(x: Float) {
        if (if (isLayoutDirectionRightToLeft) x > cancelOffset else x < -cancelOffset) {
            canceled()
            activity.viewBind.imgChatMic.translationX = 0f
            activity.viewBind.layoutSlideCancel.translationX = 0f
            return
        }
        activity.viewBind.imgChatMic.setTranslationX(x)
        activity.viewBind.layoutSlideCancel.setTranslationX(x)
        activity.viewBind.layoutLock.setTranslationY(0f)
        activity.viewBind.imgChatMic.setTranslationY(0f)
        if (Math.abs(x) < activity.viewBind.imageViewMic.getWidth() / 2) {
            if (activity.viewBind.layoutLock.visibility == View.GONE) {
                activity.viewBind.layoutLock.visibility = View.VISIBLE
            }
        } else {
            if (activity.viewBind.layoutLock.visibility == View.VISIBLE) {
                activity.viewBind.layoutLock.visibility = View.GONE
            }
        }
    }

    private fun locked() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.LOCKED)
        isLocked = true
    }

    private fun canceled() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.CANCELED)
    }

    private fun stopRecording(recordingBehaviour: RecordingBehaviour) {
        stopTrackingAction = true
        firstX = 0f
        firstY = 0f
        lastX = 0f
        lastY = 0f
        userBehaviour = UserBehaviour.NONE
        activity.viewBind.imgChatMic.animate().scaleX(1f).scaleY(1f).translationX(0f)
            .translationY(0f)
            .setDuration(100).setInterpolator(
                LinearInterpolator()
            ).start()
        activity.viewBind.layoutSlideCancel.setTranslationX(0f)
        activity.viewBind.layoutSlideCancel.setVisibility(View.GONE)
        activity.viewBind.layoutLock.setVisibility(View.GONE)
        activity.viewBind.layoutLock.setTranslationY(0f)
        activity.viewBind.imageViewLockArrow.clearAnimation()
        activity.viewBind.imageViewLock.clearAnimation()
        if (isLocked) {
            return
        }
        if (recordingBehaviour == RecordingBehaviour.LOCKED) {
            activity.viewBind.imageViewStop.setVisibility(View.VISIBLE)
            activity.viewBind.imgChatMic.setVisibility(View.GONE)
            activity.viewBind.imgChatMessageSend.setVisibility(View.GONE)
        } else if (recordingBehaviour == RecordingBehaviour.CANCELED) {
            activity.viewBind.textViewTime.clearAnimation()
            activity.viewBind.textViewTime.setVisibility(View.INVISIBLE)
            activity.viewBind.imageViewMic.setVisibility(View.INVISIBLE)
            activity.viewBind.imageViewStop.setVisibility(View.GONE)
            activity.viewBind.layoutEffect2.setVisibility(View.GONE)
            activity.viewBind.layoutEffect1.setVisibility(View.GONE)
            timerTask!!.cancel()
            delete()
        } else if (recordingBehaviour == RecordingBehaviour.RELEASED || recordingBehaviour == RecordingBehaviour.LOCK_DONE) {
            activity.viewBind.textViewTime.clearAnimation()
            activity.viewBind.textViewTime.setVisibility(View.INVISIBLE)
            activity.viewBind.imageViewMic.setVisibility(View.INVISIBLE)
            activity.viewBind.imageViewStop.setVisibility(View.GONE)
            activity.viewBind.edtChatMessage.requestFocus()
            activity.viewBind.layoutEffect2.setVisibility(View.GONE)
            activity.viewBind.layoutEffect1.setVisibility(View.GONE)
            activity.viewBind.imgChatSmiley.setVisibility(View.VISIBLE)
            activity.viewBind.edtChatMessage.setVisibility(View.VISIBLE)
            activity.viewBind.imgMessageCamera.setVisibility(View.VISIBLE)
            activity.viewBind.imgMessageAddmultimedia.setVisibility(View.VISIBLE)
            activity.viewBind.imgChatMic.setVisibility(View.VISIBLE)
            activity.viewBind.imgChatMessageSend.setVisibility(View.GONE)
            activity.viewBind.edtChatMessage.setText("")
            timerTask!!.cancel()
            // Get the audio file here
            val path = micViewBind.stopMediaRecord()
            activity.dataBind.mediaBottomSheet.sendAudio(path)
        }
    }

    private fun startRecord() {
        activity.dataBind.mediaBottomSheet.hide()
        stopTrackingAction = false
        activity.viewBind.edtChatMessage.setVisibility(View.INVISIBLE)
        activity.viewBind.imgChatSmiley.setVisibility(View.INVISIBLE)
        activity.viewBind.imgMessageCamera.setVisibility(View.INVISIBLE)
        activity.viewBind.imgMessageAddmultimedia.setVisibility(View.INVISIBLE)
        activity.viewBind.imgChatMic.animate().scaleXBy(1f).scaleYBy(1f).setDuration(200)
            .setInterpolator(
                OvershootInterpolator()
            ).start()
        activity.viewBind.textViewTime.setVisibility(View.VISIBLE)
        activity.viewBind.layoutLock.setVisibility(View.VISIBLE)
        activity.viewBind.layoutSlideCancel.setVisibility(View.VISIBLE)
        activity.viewBind.imageViewMic.setVisibility(View.VISIBLE)
        activity.viewBind.layoutEffect2.setVisibility(View.VISIBLE)
        activity.viewBind.layoutEffect1.setVisibility(View.VISIBLE)
        activity.viewBind.textViewTime.startAnimation(micViewBind.animBlink)
        activity.viewBind.imageViewLockArrow.clearAnimation()
        activity.viewBind.imageViewLock.clearAnimation()
        activity.viewBind.imageViewLockArrow.startAnimation(micViewBind.animJumpFast)
        activity.viewBind.imageViewLock.startAnimation(micViewBind.animJump)
        if (audioTimer == null) {
            audioTimer = Timer()
            timeFormatter!!.timeZone = TimeZone.getTimeZone("UTC")
        }
        timerTask = object : TimerTask() {
            override fun run() {
                handler!!.post {
                    activity.viewBind.textViewTime.setText(
                        timeFormatter!!.format(
                            Date(
                                audioTotalTime * 1000L
                            )
                        )
                    )
                    audioTotalTime++
                }
            }
        }
        audioTotalTime = 0
        audioTimer?.schedule(timerTask, 0, 1000)
        micViewBind.recordMedia()
    }

    private fun delete() {
        activity.viewBind.imageViewMic.setVisibility(View.VISIBLE)
        activity.viewBind.imageViewMic.setRotation(0f)
        isDeleting = true
        activity.viewBind.imgChatMic.setEnabled(false)
        micViewBind.deleteAudioFile()
        handler!!.postDelayed({
            isDeleting = false
            activity.viewBind.imgChatMic.setEnabled(true)
            activity.viewBind.imgChatSmiley.setVisibility(View.VISIBLE)
            activity.viewBind.edtChatMessage.setVisibility(View.VISIBLE)
            activity.viewBind.imgMessageCamera.setVisibility(View.VISIBLE)
            activity.viewBind.imgMessageAddmultimedia.setVisibility(View.VISIBLE)
            //            activity.viewBind.imgChatMic.setVisibility(View.VISIBLE);
//            activity.viewBind.img_chat_message_send.setVisibility(View.GONE);
            activity.viewBind.edtChatMessage.setText("")
        }, 1500)
        activity.viewBind.imageViewMic.animate().translationY(-dp * 150).rotation(180f)
            .scaleXBy(0.6f).scaleYBy(0.6f).setDuration(500).setInterpolator(
                DecelerateInterpolator()
            ).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    var displacement = 0f
                    displacement = -dp * 40
                    activity.viewBind.dustin.setTranslationX(displacement)
                    activity.viewBind.dustinCover.setTranslationX(displacement)
                    activity.viewBind.dustinCover.animate().translationX(0f).rotation(-120f)
                        .setDuration(350)
                        .setInterpolator(
                            DecelerateInterpolator()
                        ).start()
                    activity.viewBind.dustin.animate().translationX(0f).setDuration(350)
                        .setInterpolator(
                            DecelerateInterpolator()
                        ).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                activity.viewBind.dustin.setVisibility(View.VISIBLE)
                                activity.viewBind.dustinCover.setVisibility(View.VISIBLE)
                            }

                            override fun onAnimationEnd(animation: Animator) {}
                            override fun onAnimationCancel(animation: Animator) {}
                            override fun onAnimationRepeat(animation: Animator) {}
                        }).start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    activity.viewBind.imageViewMic.animate().translationY(0f).scaleX(1f).scaleY(1f)
                        .setDuration(350).setInterpolator(
                            LinearInterpolator()
                        ).setListener(
                            object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}
                                override fun onAnimationEnd(animation: Animator) {
                                    activity.viewBind.imageViewMic.visibility = View.INVISIBLE
                                    activity.viewBind.imageViewMic.rotation = 0f
                                    var displacement = 0f
                                    displacement = -dp * 40
                                    activity.viewBind.dustinCover.animate().rotation(0f)
                                        .setDuration(150)
                                        .setStartDelay(50).start()
                                    activity.viewBind.dustin.animate().translationX(displacement)
                                        .setDuration(200)
                                        .setStartDelay(250).setInterpolator(
                                            DecelerateInterpolator()
                                        ).start()
                                    activity.viewBind.dustinCover.animate()
                                        .translationX(displacement)
                                        .setDuration(200).setStartDelay(250).setInterpolator(
                                            DecelerateInterpolator()
                                        ).setListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(animation: Animator) {}
                                            override fun onAnimationEnd(animation: Animator) {
                                                activity.viewBind.edtChatMessage.setVisibility(
                                                    View.VISIBLE
                                                )
                                                activity.viewBind.edtChatMessage.requestFocus()
                                            }

                                            override fun onAnimationCancel(animation: Animator) {}
                                            override fun onAnimationRepeat(animation: Animator) {}
                                        }).start()
                                }

                                override fun onAnimationCancel(animation: Animator) {}
                                override fun onAnimationRepeat(animation: Animator) {}
                            }
                        ).start()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
    }
}
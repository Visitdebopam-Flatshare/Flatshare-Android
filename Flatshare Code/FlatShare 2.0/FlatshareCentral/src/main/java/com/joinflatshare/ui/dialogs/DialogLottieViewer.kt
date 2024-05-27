package com.joinflatshare.ui.dialogs

import android.animation.Animator
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.DialogLottieViewerBinding
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 24/03/23
 */
class DialogLottieViewer(
    activity: ComponentActivity,
    animation: Int,
    callback: OnStringFetched?
) {
    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = DialogLottieViewerBinding.inflate(activity.layoutInflater)
        builder.setView(holder.root)
        builder.setCancelable(true)
        dialog = builder.create()
        loadAnimation(holder.lottieDialogView, animation, callback)
        dialog?.show()
    }

    companion object {
        var dialog: AlertDialog? = null
        fun loadAnimation(
            lottieView: LottieAnimationView,
            animation: Int,
            callBack: OnStringFetched?
        ) {
            lottieView.setAnimation(animation)
            lottieView.visibility = View.VISIBLE
            lottieView.playAnimation()
            lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                }

                override fun onAnimationEnd(animator: Animator) {
                    lottieView.visibility = View.GONE
                    if (dialog != null && dialog!!.isShowing)
                        dialog?.dismiss()
                    callBack?.onFetched("1");
                }

                override fun onAnimationCancel(animator: Animator) {
                }

                override fun onAnimationRepeat(animator: Animator) {
                }

            })
        }
    }
}
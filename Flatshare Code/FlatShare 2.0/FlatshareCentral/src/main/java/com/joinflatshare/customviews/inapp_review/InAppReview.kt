package com.joinflatshare.customviews.inapp_review

import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.play.core.review.ReviewManagerFactory
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 18/02/23
 */
object InAppReview {

    fun show(activity: BaseActivity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    MixpanelUtils.sendToMixPanel("In App Review Asked")
                }

            } else {
                // There was some problem, log or handle the error code.
                val exception = task.exception as RuntimeExecutionException
                Logger.log("Failed to show review API ${exception.message}", Logger.LOG_TYPE_ERROR)
            }
        }
    }
}
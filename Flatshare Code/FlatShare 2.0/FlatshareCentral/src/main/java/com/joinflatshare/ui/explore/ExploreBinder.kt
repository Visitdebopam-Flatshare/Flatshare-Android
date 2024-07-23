package com.joinflatshare.ui.explore

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.TranslateAnimation
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.ui.explore.holder.AdapterUserHolder


class ExploreBinder(
    private val activity: ExploreActivity,
) {
    private val viewBind = activity.viewBind

    init {
        hideAll()
    }

    fun hideAll() {
        viewBind.llNoFeed.visibility = View.GONE
        viewBind.frameViewHolder.visibility = View.GONE
    }

    fun setup() {
        hideAll()
        activity.apiController?.hasMoreData = true
        activity.apiController?.currentPage = 0
        activity.apiController?.getRecommendedFHTUsers()
    }

    fun showEmptyData(text: String, button: String) {
        viewBind.llNoFeed.visibility = View.VISIBLE
        viewBind.frameViewHolder.visibility = View.GONE
        viewBind.imgNoFeed.setImageResource(R.drawable.ic_no_feed)
        if (text.isNotEmpty()) viewBind.txtNoFeed.text = text
        if (button.isNotEmpty()) viewBind.btnNoFeed.text = button
    }

    fun showContentData() {
        viewBind.llNoFeed.visibility = View.GONE
        viewBind.frameViewHolder.visibility = View.VISIBLE
    }

    fun showUser() {
        if (activity.userData.size == 0) {
            if (activity.apiController?.hasMoreData == true) {
                // There is more data to call
                activity.apiController?.getRecommendedFHTUsers()
            } else {
                // There is no more data
                showEmptyData(
                    "Sorry, we’ve run out of potential flatmates for you.",
                    "Edit Preferences"
                )
            }
        } else {
            animateView()

            // Get the topmost data
            val user = activity.userData[0]
            AdapterUserHolder().bindUser(activity, user.data!!, viewBind.includePagerExplore)
        }
    }

    private fun animateView() {
        // hide View
        viewBind.frameViewHolder.visibility = View.GONE
        val animate = TranslateAnimation(0f, 0f, viewBind.frameViewHolder.height.toFloat(), -100f)
        animate.duration = 300
        viewBind.frameViewHolder.startAnimation(animate)

        Handler(Looper.getMainLooper()).postDelayed({ viewBind.frameViewHolder.visibility = View.VISIBLE }, 1000)


        /*viewBind.frameViewHolder.visibility = View.GONE
        // Show View
        viewBind.frameViewHolder.visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, viewBind.frameViewHolder.height.toFloat(), 0f)
        animate.duration = 500
        animate.fillAfter = true
        viewBind.frameViewHolder.startAnimation(animate)*/
    }
}
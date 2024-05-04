package com.joinflatshare.ui.explore

import android.view.View
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.explore.holder.AdapterUserHolder
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper


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
            // Get the topmost data
            val user = activity.userData[0]
            AdapterUserHolder(activity).bindUser(user.data, viewBind.includePagerExplore)

            // Call next page for pagination
            /*if (activity.apiController?.hasMoreData == true) {
                activity.apiController?.getRecommendedFHTUsers()
            }*/
        }
    }

    private fun isDealBreakersEmpty(dealBreakers: DealBreakers?): Boolean {
        if (dealBreakers == null)
            return true
        val smoking = dealBreakers.smoking
        val party = dealBreakers.flatparty
        val eggs = dealBreakers.eggs
        val pets = dealBreakers.pets
        val workout = dealBreakers.workout
        return (smoking == 1 && party == 1 && eggs == 1 && pets == 1 && workout == 1)
    }


}
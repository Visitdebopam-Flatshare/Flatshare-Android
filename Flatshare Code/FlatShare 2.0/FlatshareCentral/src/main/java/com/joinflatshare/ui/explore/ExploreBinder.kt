package com.joinflatshare.ui.explore

import android.view.View
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.ui.explore.holder.AdapterUserHolder


class ExploreBinder(
    private val activity: ExploreActivity,
) {
    private val viewBind = activity.viewBind
    private val adapter: ExploreAdapter

    init {
        viewBind.rvExplore.layoutManager = LinearLayoutManager(activity)
        adapter = ExploreAdapter(activity, activity.userData)
        viewBind.rvExplore.adapter = adapter

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

    fun showEmptyData(image: Int, text: String, button: String) {
        viewBind.llNoFeed.visibility = View.VISIBLE
        viewBind.frameViewHolder.visibility = View.GONE
        viewBind.imgNoFeed.setImageResource(image)
        if (text.isNotEmpty()) viewBind.txtNoFeed.text = text
        if (button.isNotEmpty()) viewBind.btnNoFeed.text = button
    }

    fun showContentData() {
        viewBind.llNoFeed.visibility = View.GONE
        viewBind.frameViewHolder.visibility = View.VISIBLE
        viewBind.btnExploreLoad.visibility =
            if (activity.apiController?.hasMoreData == true) View.VISIBLE else View.GONE
        adapter.notifyDataSetChanged()
    }
}
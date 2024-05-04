package com.joinflatshare.ui.explore.holder

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.liked.LikedActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserVpHolder(private val holder: ItemExploreVpBinding) :
    RecyclerView.ViewHolder(holder.root) {
    fun bindVp(
        activity: BaseActivity,
        vpSlide: Int,
        user: User,
        loggedInUserFlat: MyFlatData?,
        callback: OnUiEventClick
    ) {
        when (vpSlide) {
            ExploreAdapter.VP_SLIDE_INTEREST -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.VISIBLE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Interests"
                val properties = user.flatProperties
                val interestsView = InterestsView(
                    activity, holder.rvExploreVp,
                    InterestsView.VIEW_TYPE_INTERESTS
                )
                interestsView.setContentValues(properties.interests)
                interestsView.calculateMatchingContent(loggedInUserFlat?.flatProperties?.interests)
                interestsView.show()
            }

            ExploreAdapter.VP_SLIDE_DEAL_BREAKERS -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.VISIBLE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Deal Breakers"

                val dealBreakerView = DealBreakerView(activity, holder.rvExploreVp)
                dealBreakerView.setDealValues(user.flatProperties, null)
                dealBreakerView.show()
            }

            ExploreAdapter.VP_SLIDE_LANGUAGES -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.VISIBLE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Languages"
                val properties = user.flatProperties
                val interestsView = InterestsView(
                    activity, holder.rvExploreVp,
                    InterestsView.VIEW_TYPE_LANGUAGES
                )
                interestsView.setContentValues(properties.languages)
                interestsView.calculateMatchingContent(loggedInUserFlat?.flatProperties?.languages)
                interestsView.show()
            }

            ExploreAdapter.VP_SLIDE_ABOUT -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.GONE
                holder.txtExploreVpDesc.visibility = View.VISIBLE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "About ${user.name?.firstName} & Looking for"
                holder.txtExploreVpDesc.text = user.status
            }

            ExploreAdapter.VP_SLIDE_WORK -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.GONE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.VISIBLE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "About ${user.name?.firstName}"

                if (user.work.isNullOrBlank()) {
                    holder.llProfileWork.visibility = View.GONE
                } else {
                    holder.llProfileWork.visibility = View.VISIBLE
                    holder.txtProfileWork.text = user.work
                }
                if (user.hometown == null || user.hometown!!.name.isBlank()) holder.llProfileHometown.visibility =
                    View.GONE
                else {
                    holder.llProfileHometown.visibility = View.VISIBLE
                    holder.txtProfileHometown.text = user.hometown?.name
                }

                if (user.college == null || user.college!!.name.isBlank()) holder.llProfileCollege.visibility =
                    View.GONE
                else {
                    holder.llProfileCollege.visibility = View.VISIBLE
                    holder.txtProfileCollege.text = user.college?.name
                }

                if (user.hangout == null || user.hangout!!.name.isBlank()) holder.llProfileHangout.visibility =
                    View.GONE
                else {
                    holder.llProfileHangout.visibility = View.VISIBLE
                    holder.txtProfileHangout.text = user.hangout?.name
                }
            }
        }
        holder.exploreFrameVp.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, RouteConstants.ROUTE_CONSTANT_FEED)
            intent.putExtra("phone", user.id)
            if (activity is ExploreActivity)
                intent.putExtra("searchType", activity.SEARCH_TYPE)
            else if (activity is LikedActivity)
                intent.putExtra("searchType", activity.SEARCH_TYPE)
            CommonMethod.switchActivity(
                activity,
                intent
            ) { result ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    callback.onClick(result.data, 101)
                }
            }
        }
    }
}
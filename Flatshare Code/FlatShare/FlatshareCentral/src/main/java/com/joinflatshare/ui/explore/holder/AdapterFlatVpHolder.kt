package com.joinflatshare.ui.explore.holder

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.explore.FlatRecommendationItem
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.flat.edit.FlatMemberAdapter
import com.joinflatshare.ui.liked.LikedActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.helper.DistanceCalculator

/**
 * Created by debopam on 14/11/22
 */
class AdapterFlatVpHolder(private val holder: ItemExploreVpBinding) :
    RecyclerView.ViewHolder(holder.root) {
    fun bindVp(
        activity: BaseActivity,
        vpSlide: Int,
        data: FlatRecommendationItem,
        flat: MyFlatData,
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
                val properties = flat.flatProperties
                val interestsView = InterestsView(
                    activity, holder.rvExploreVp,
                    InterestsView.VIEW_TYPE_INTERESTS
                )
                interestsView.setContentValues(properties.interests)
                interestsView.calculateMatchingContent(AppConstants.loggedInUser?.flatProperties?.interests!!)
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
                dealBreakerView.setDealValues(flat.flatProperties, null)

                dealBreakerView.show()
            }

            ExploreAdapter.VP_SLIDE_LANGUAGES -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.VISIBLE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Languages"

                val properties = flat.flatProperties
                val interestsView = InterestsView(
                    activity, holder.rvExploreVp,
                    InterestsView.VIEW_TYPE_LANGUAGES
                )
                interestsView.setContentValues(properties.languages)
                interestsView.calculateMatchingContent(AppConstants.loggedInUser?.flatProperties?.languages!!)
                interestsView.show()
            }

            ExploreAdapter.VP_SLIDE_NORMS -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.GONE
                holder.txtExploreVpDesc.visibility = View.VISIBLE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Flat Norms & Expenses"
                holder.txtExploreVpDesc.text = flat.norms
            }

            ExploreAdapter.VP_SLIDE_FLATMATES -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.VISIBLE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.GONE

                holder.txtExploreVpName.text = "Flatmates"
                holder.rvExploreVp.layoutManager = LinearLayoutManager(
                    activity,
                    RecyclerView.HORIZONTAL,
                    false
                )
                holder.rvExploreVp.adapter = FlatMemberAdapter(
                    activity,
                    flat,
                    flat.flatMates
                )
            }

            ExploreAdapter.VP_SLIDE_FLATDETAILS -> {
                holder.txtExploreVpName.visibility = View.VISIBLE
                holder.rvExploreVp.visibility = View.GONE
                holder.txtExploreVpDesc.visibility = View.GONE
                holder.llExploreVpWork.visibility = View.GONE
                holder.llExploreVpFlatDetails.visibility = View.VISIBLE

                holder.txtExploreVpName.text = "Flat Details"

                val properties = flat.flatProperties
                // Gender
                val gender = properties.gender
                if (gender.isNullOrBlank() || gender.equals("Both")) {
                    holder.imgGender.setImageResource(R.drawable.ic_gender_both)
                    holder.txtGender.text = "Gender-Neutral"
                } else if (gender.equals("Male")) {
                    holder.imgGender.setImageResource(R.drawable.ic_male)
                    holder.txtGender.text = "Male Only"
                } else if (gender.equals("Female")) {
                    holder.imgGender.setImageResource(R.drawable.ic_female)
                    holder.txtGender.text = "Female Only"
                }

                // Location
                val details = data.details
                if (activity is LikedActivity) {
                    if (details.coordinates.isNullOrEmpty()
                        || properties.location?.loc?.coordinates.isNullOrEmpty()
                    )
                        holder.txtFlatLocation.text = "NA"
                    else holder.txtFlatLocation.text = DistanceCalculator.calculateDistance(
                        details.coordinates[0],
                        details.coordinates[1],
                        properties.location?.loc?.coordinates!![0],
                        properties.location?.loc?.coordinates!![1]
                    )
                } else {
                    if (details.preferredLocation.isNullOrEmpty()
                        || details.flatLocationMatch.isNullOrEmpty()
                    )
                        holder.txtFlatLocation.text = "NA"
                    else holder.txtFlatLocation.text = "${details.distance} km"
                    /*DistanceCalculator.calculateDistance(
                        details.preferredLocation[0],
                        details.preferredLocation[1],
                        details.flatLocationMatch[0],
                        details.flatLocationMatch[1]
                    )*/
                }

                // Rent
                if (properties.rentperPerson == 0) {
                    holder.txtRent.text = "NA"
                    holder.txtRentPerPerson.visibility = View.INVISIBLE
                } else {
                    holder.txtRent.text =
                        activity.resources.getString(R.string.currency) + properties.rentperPerson
                    holder.txtRentPerPerson.visibility = View.VISIBLE
                }

                // Deposit
                if (properties.depositperPerson == 0) {
                    holder.txtDeposit.text = "NA"
                    holder.txtDepositPerPerson.visibility = View.INVISIBLE
                } else {
                    holder.txtDeposit.text =
                        activity.resources.getString(R.string.currency) + properties.depositperPerson
                    holder.txtDepositPerPerson.visibility = View.VISIBLE
                }

                // Available
                if (properties.moveinDate.isNullOrEmpty()) {
                    holder.txtDate.text = "NA"
                } else {
                    holder.txtDate.text = DateUtils.convertToAppFormat(properties.moveinDate)
                }
            }
        }
        holder.exploreFrameVp.setOnClickListener {
            val intent = Intent(activity, FlatDetailsActivity::class.java)
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, RouteConstants.ROUTE_CONSTANT_FEED)
            intent.putExtra("phone", flat.mongoId)
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
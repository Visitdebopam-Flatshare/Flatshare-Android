package com.joinflatshare.ui.explore.holder

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.provider.ContactsContract.Profile
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.card.MaterialCardView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksVpBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.adapter.ExploreImageAdapter
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper
import jp.wasabeef.blurry.Blurry

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserVpHolder(private val view: ViewBinding) : RecyclerView.ViewHolder(view.root) {
    fun bindVp(activity: ExploreActivity, vpSlide: Int, user: User) {
        val holder = view as ItemExploreVpBinding
        when (vpSlide) {
            AdapterUserHolder.VP_SLIDE_ABOUT -> {
                holder.llVp1.visibility = View.VISIBLE
                holder.llVp2.visibility = View.GONE

                if (user.status.isNullOrEmpty()) holder.llVpAbout.visibility = View.GONE
                else {
                    holder.llVpAbout.visibility = View.VISIBLE
                    holder.txtAbout.text = user.status
                }

                if (user.flatProperties.interests.isNullOrEmpty()) holder.llVpInterest.visibility =
                    View.GONE
                else {
                    holder.llVpInterest.visibility = View.VISIBLE
                    holder.txtInterest.text = TextUtils.join(", ", user.flatProperties.interests)
                }

                if (user.flatProperties.languages.isNullOrEmpty()) holder.llVpLanguage.visibility =
                    View.GONE
                else {
                    holder.llVpLanguage.visibility = View.VISIBLE
                    holder.txtLanguage.text = TextUtils.join(", ", user.flatProperties.languages)
                }

                if (CommonMethod.isDealBreakerEmpty(user.flatProperties.dealBreakers)) {
                    holder.llVpDeals.visibility = View.GONE
                } else {
                    holder.llVpDeals.visibility = View.VISIBLE
                    val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
                    dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
                    dealBreakerView.show()
                }


            }

            AdapterUserHolder.VP_SLIDE_WORK -> {
                holder.llVp1.visibility = View.GONE
                holder.llVp2.visibility = View.VISIBLE

                if (user.work.isNullOrEmpty()) holder.llVpWork.visibility = View.GONE
                else {
                    holder.llVpWork.visibility = View.VISIBLE
                    holder.txtWork.text = user.work
                }

                if (user.college?.name.isNullOrEmpty()) holder.llVpEducation.visibility = View.GONE
                else {
                    holder.llVpEducation.visibility = View.VISIBLE
                    holder.txtEducation.text = user.college?.name
                }

                if (user.hometown?.name.isNullOrEmpty()) holder.llVpHometown.visibility = View.GONE
                else {
                    holder.llVpHometown.visibility = View.VISIBLE
                    holder.txtHometown.text = user.hometown?.name
                }

                if (user.score <= 0) holder.llVpScore.visibility = View.GONE
                else {
                    holder.llVpScore.visibility = View.VISIBLE
                    holder.txtScore.text = "" + user.score
                }

                // Images
                holder.cardGallery.visibility = View.GONE
                if (!user.images.isNullOrEmpty()) {
                    if ((!user.dp.isNullOrBlank() && user.images.size >= 2) || user.images.size >= 3) {
                        // Show gallery button
                        holder.cardGallery.visibility = View.VISIBLE
                        holder.cardGallery.setOnClickListener {
                            val intent = Intent(activity, ProfileDetailsActivity::class.java)
                            intent.putExtra("phone", user.id)
                            CommonMethod.switchActivity(activity, intent, false)
                        }
                    }
                }

            }
        }

        val completion = AppConstants.loggedInUser?.completed
        if (completion == null || completion < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
//                blurView(activity, holder)
        }
    }


    fun bindVp(
        activity: ChecksActivity, user: User
    ) {
        val holder = view as ItemExploreVpBinding
        holder.llVp1.visibility = View.VISIBLE
        holder.llVp2.visibility = View.GONE

        if (user.status.isNullOrEmpty()) holder.llVpAbout.visibility = View.GONE
        else {
            holder.llVpAbout.visibility = View.VISIBLE
            holder.txtAbout.text = user.status
        }

        if (user.flatProperties.interests.isNullOrEmpty()) holder.llVpInterest.visibility =
            View.GONE
        else {
            holder.llVpInterest.visibility = View.VISIBLE
            holder.txtInterest.text = TextUtils.join(", ", user.flatProperties.interests)
        }

        if (user.flatProperties.languages.isNullOrEmpty()) holder.llVpLanguage.visibility =
            View.GONE
        else {
            holder.llVpLanguage.visibility = View.VISIBLE
            holder.txtLanguage.text = TextUtils.join(", ", user.flatProperties.languages)
        }

        if (CommonMethod.isDealBreakerEmpty(user.flatProperties.dealBreakers)) {
            holder.llVpDeals.visibility = View.GONE
        } else {
            holder.llVpDeals.visibility = View.VISIBLE
            val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
            dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
            dealBreakerView.show()
        }
    }

    private fun blurView(activity: BaseActivity, holder: ItemExploreVpBinding) {
        Blurry.with(activity).radius(25).sampling(4)
            .color(ContextCompat.getColor(activity, R.color.white)).capture(holder.feedVpHolder)
            .getAsync {
                holder.imgBlur.setImageDrawable(BitmapDrawable(activity.resources, it))
            }
    }

    private fun isLocationEmpty(location: ModelLocation?): Boolean {
        return (location?.loc == null || location.loc.coordinates.isNullOrEmpty())
    }
}
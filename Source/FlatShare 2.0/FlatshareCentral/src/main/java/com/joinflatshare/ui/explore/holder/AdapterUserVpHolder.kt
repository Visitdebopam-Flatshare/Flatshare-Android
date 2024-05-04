package com.joinflatshare.ui.explore.holder

import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreImageAdapter
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserVpHolder(private val holder: ItemExploreVpBinding) :
    RecyclerView.ViewHolder(holder.root) {
    fun bindVp(
        activity: BaseActivity,
        vpSlide: Int,
        user: User
    ) {
        when (vpSlide) {
            AdapterUserHolder.VP_SLIDE_ABOUT -> {
                holder.llVp1.visibility = View.VISIBLE
                holder.llVp2.visibility = View.GONE
                holder.llVp3.visibility = View.GONE

                ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

                // Name
                holder.txtName.text =
                    "${user.name?.firstName} ${user.name?.lastName}, ${CommonMethod.getAge(user.dob)}"


                // Age
                if (isLocationEmpty(AppConstants.loggedInUser?.location) || isLocationEmpty(user.location))
                    holder.txtDistance.visibility = View.GONE
                else {
                    holder.txtDistance.visibility = View.VISIBLE
                    holder.txtDistance.text = (DistanceCalculator.calculateDistance(
                        user.location.loc.coordinates[1],
                        user.location.loc.coordinates[0],
                        AppConstants.loggedInUser?.location?.loc?.coordinates!![1],
                        AppConstants.loggedInUser?.location?.loc?.coordinates!![0]
                    )) + " away"
                }

                if (user.status.isNullOrEmpty())
                    holder.llVpAbout.visibility = View.GONE
                else {
                    holder.llVpAbout.visibility = View.VISIBLE
                    holder.txtAbout.text = user.status
                }

                if (user.flatProperties.interests.isNullOrEmpty())
                    holder.llVpInterest.visibility = View.GONE
                else {
                    holder.llVpInterest.visibility = View.VISIBLE
                    holder.txtInterest.text = TextUtils.join(", ", user.flatProperties.interests)
                }

                if (user.flatProperties.languages.isNullOrEmpty())
                    holder.llVpLanguage.visibility = View.GONE
                else {
                    holder.llVpLanguage.visibility = View.VISIBLE
                    holder.txtLanguage.text = TextUtils.join(", ", user.flatProperties.languages)
                }

                if (user.flatProperties.dealBreakers == null) {
                    holder.llVpDeals.visibility = View.GONE
                } else {
                    holder.llVpDeals.visibility = View.VISIBLE
                    val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
                    dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
                    dealBreakerView.show()
                }

                /*val interestsView = InterestsView(
                    activity, holder.rvExploreVp,
                    InterestsView.VIEW_TYPE_INTERESTS,false
                )
                interestsView.setContentValues(properties.interests)
                interestsView.calculateMatchingContent(loggedInUserFlat?.flatProperties?.interests)
                interestsView.show()*/
            }

            AdapterUserHolder.VP_SLIDE_WORK -> {
                holder.llVp1.visibility = View.GONE
                holder.llVp2.visibility = View.VISIBLE
                holder.llVp3.visibility = View.GONE

                if (user.work.isNullOrEmpty())
                    holder.llVpWork.visibility = View.GONE
                else {
                    holder.llVpWork.visibility = View.VISIBLE
                    holder.txtWork.text = user.work
                }

                if (user.college?.name.isNullOrEmpty())
                    holder.llVpEducation.visibility = View.GONE
                else {
                    holder.llVpEducation.visibility = View.VISIBLE
                    holder.txtEducation.text = user.college?.name
                }

                if (user.hometown?.name.isNullOrEmpty())
                    holder.llVpHometown.visibility = View.GONE
                else {
                    holder.llVpHometown.visibility = View.VISIBLE
                    holder.txtHometown.text = user.hometown?.name
                }

                if (user.score <= 0)
                    holder.llVpScore.visibility = View.GONE
                else {
                    holder.llVpScore.visibility = View.VISIBLE
                    holder.txtScore.text = "" + user.score
                }
            }

            AdapterUserHolder.VP_SLIDE_IMAGES -> {
                holder.llVp1.visibility = View.GONE
                holder.llVp2.visibility = View.GONE
                holder.llVp3.visibility = View.VISIBLE
                val images = ArrayList<String>()
                /*if (!user.dp.isNullOrEmpty())
                    images.add(user.dp!!)
                if (!user.images.isNullOrEmpty()) {
                    if (images.isEmpty()) {
                        if (user.images.size >= 4) {
                            images.add(user.images[0])
                            images.add(user.images[1])
                            images.add(user.images[2])
                            images.add(user.images[3])
                        } else {
                            images.addAll(user.images)
                        }
                    } else {
                        if (user.images.size >= 3) {
                            images.add(user.images[0])
                            images.add(user.images[1])
                            images.add(user.images[2])
                        } else {
                            images.addAll(user.images)
                        }
                    }
                }*/
                images.add(AppConstants.loggedInUser?.dp!!)
                images.addAll(AppConstants.loggedInUser?.images!!)
                holder.llExplorePhoto1.visibility = View.GONE
                when (images.size) {
                    1, 2, 3, 4 -> {
                        holder.llExplorePhoto1.visibility = View.VISIBLE
                        resizeImageViews(activity, holder.llExplorePhoto1, 0, images)
                    }

                    /*2 -> {

                    }

                    3 -> {

                    }

                    4 -> {

                    }*/
                }


                /*  val spanCount = if (user.dp.isNullOrEmpty()) {
                      if (user.images.size == 1) 1 else 2
                  } else {
                      if (user.images.isNullOrEmpty()) 1 else 2
                  }
                  val spacing = 25
                  val includeEdge = false
                  holder.rvExploreImages.addItemDecoration(
                      GridSpacingItemDecoration(
                          spanCount,
                          spacing,
                          includeEdge
                      )
                  )
                  holder.rvExploreImages.layoutManager = GridLayoutManager(activity, spanCount)

                  holder.rvExploreImages.adapter = ExploreImageAdapter(activity, images)*/

            }
        }

    }

    private fun resizeImageViews(
        activity: BaseActivity,
        ll: LinearLayout,
        width: Int,
        images: ArrayList<String>
    ) {
        var imageViewCount = 0
        for (view in ll.children) {
            if (view is ImageView) {
                /*if (width == 0) {
                    view.viewTreeObserver.addOnGlobalLayoutListener(object :
                        OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            val height = view.height
                            val w = (height / 16) * 9
                            resizeImageViews(activity, ll, w, images)
                        }
                    })
                } else {
                    view.layoutParams =
                        LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT)*/
                ImageHelper.loadImage(
                    activity,
                    0,
                    view,
                    ImageHelper.getUserImagesWithAws(images[imageViewCount++])
                )
//                }
            }
        }
    }

    private fun isLocationEmpty(location: ModelLocation?): Boolean {
        return (location?.loc == null || location.loc.coordinates.isNullOrEmpty())
    }
}
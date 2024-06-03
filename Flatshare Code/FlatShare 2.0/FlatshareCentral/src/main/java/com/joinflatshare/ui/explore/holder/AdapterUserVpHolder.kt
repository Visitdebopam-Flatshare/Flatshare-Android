package com.joinflatshare.ui.explore.holder

import android.content.Intent
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
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.adapter.ExploreImageAdapter
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserVpHolder(private val view: ViewBinding) :
    RecyclerView.ViewHolder(view.root) {
    fun bindVp(
        activity: BaseActivity,
        vpSlide: Int,
        user: User
    ) {
        if (activity is ExploreActivity) {
            val holder = view as ItemExploreVpBinding
            when (vpSlide) {
                AdapterUserHolder.VP_SLIDE_ABOUT -> {
                    holder.llVp1.visibility = View.VISIBLE
                    holder.llVp2.visibility = View.GONE
                    holder.llVp3.visibility = View.GONE

                    ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)
                    holder.imgProfile.setOnClickListener {
                        val intent = Intent(activity, ProfileDetailsActivity::class.java)
                        intent.putExtra("phone", user.id)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
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
                        holder.txtInterest.text =
                            TextUtils.join(", ", user.flatProperties.interests)
                    }

                    if (user.flatProperties.languages.isNullOrEmpty())
                        holder.llVpLanguage.visibility = View.GONE
                    else {
                        holder.llVpLanguage.visibility = View.VISIBLE
                        holder.txtLanguage.text =
                            TextUtils.join(", ", user.flatProperties.languages)
                    }

                    if (CommonMethod.isDealBreakerEmpty(user.flatProperties.dealBreakers)) {
                        holder.llVpDeals.visibility = View.GONE
                    } else {
                        holder.llVpDeals.visibility = View.VISIBLE
                        val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
                        dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
                        dealBreakerView.show()
                    }

                    for (i in 0 until holder.llVp1Scroll.childCount step 2) {
                        if (i == 0)
                            (holder.llVp1Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp1Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
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

                    for (i in 0 until holder.llVp2Scroll.childCount step 2) {
                        if (i == 2)
                            (holder.llVp2Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp2Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
                }

                AdapterUserHolder.VP_SLIDE_IMAGES -> {
                    holder.llVp1.visibility = View.GONE
                    holder.llVp2.visibility = View.GONE
                    holder.llVp3.visibility = View.VISIBLE
                    var images = ArrayList<String>()


                    if (!user.dp.isNullOrEmpty())
                        images.add(user.dp!!)
                    if (!user.images.isNullOrEmpty()) {
                        images.addAll(user.images)
                    }
                    images = removeExtraImagesFromList(images)


                    holder.includeExploreImage.llExplorePhotoContainer1.visibility = View.GONE
                    holder.includeExploreImage.llExplorePhotoContainer2.visibility = View.GONE
                    holder.includeExploreImage.llExplorePhotoContainer3.visibility = View.GONE
                    when (images.size) {
                        1 -> {
                            holder.includeExploreImage.llExplorePhotoContainer1.visibility =
                                View.VISIBLE
                            val ll = holder.includeExploreImage.llExploreImageHolder
                            ll.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    ll.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val height = ll.height
                                    val width = ll.width
                                    val params = if (width < height)
                                        LinearLayout.LayoutParams(width, width)
                                    else LinearLayout.LayoutParams(height, height)
                                    holder.includeExploreImage.llExplorePhotoContainer1.layoutParams =
                                        params

                                }
                            })
                            val img =
                                ((holder.includeExploreImage.llExplorePhotoContainer1.getChildAt(0)
                                        as MaterialCardView).getChildAt(0)
                                        as ImageView)
                            ImageHelper.loadImage(
                                activity,
                                0,
                                img,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                        }

                        2 -> {
                            holder.includeExploreImage.llExplorePhotoContainer2.visibility =
                                View.VISIBLE
                            val card1 =
                                holder.includeExploreImage.llExplorePhotoContainer2.getChildAt(0) as MaterialCardView
                            val card2 =
                                holder.includeExploreImage.llExplorePhotoContainer2.getChildAt(2) as MaterialCardView
                            card1.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    card1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val width = card1.width
                                    val height = (width / 9) * 12
                                    card1.layoutParams.height = height
                                    card2.layoutParams.height = height
                                    CommonMethod.makeLog("Width", "" + width + "/" + height)
                                }
                            })
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card1.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card2.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[1]
                            )
                        }

                        3 -> {
                            holder.includeExploreImage.llExplorePhotoContainer3.visibility =
                                View.VISIBLE
                            val card1 =
                                (holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(0) as LinearLayout).getChildAt(
                                    0
                                ) as MaterialCardView
                            val card2 =
                                (holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(0) as LinearLayout).getChildAt(
                                    2
                                ) as MaterialCardView
                            val card3 =
                                holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(2) as MaterialCardView
                            card1.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    card1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val height = card1.height
                                    val width = ((height / 16) * 9)
                                    val params = LinearLayout.LayoutParams(width, height)
//                                card1.layoutParams = params
//                                card2.layoutParams = params
                                    card3.layoutParams = LinearLayout.LayoutParams(
                                        width,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1f
                                    )
                                    CommonMethod.makeLog("Width", "" + width + "/" + height)
                                }
                            })
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card1.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card2.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[1]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card3.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[2]
                            )
                        }

                        /*4 -> {

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

                    for (i in 0 until holder.llVp3Scroll.childCount step 2) {
                        if (i == 4)
                            (holder.llVp3Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp3Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
                }

            }
        } else {
            val holder = view as ItemChecksVpBinding
            when (vpSlide) {
                AdapterUserHolder.VP_SLIDE_ABOUT -> {
                    holder.llVp1.visibility = View.VISIBLE
                    holder.llVp2.visibility = View.GONE
                    holder.llVp3.visibility = View.GONE

                    ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)
                    holder.imgProfile.setOnClickListener {
                        val intent = Intent(activity, ProfileDetailsActivity::class.java)
                        intent.putExtra("phone", user.id)
                        CommonMethod.switchActivity(activity, intent, false)
                    }

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
                        holder.txtInterest.text =
                            TextUtils.join(", ", user.flatProperties.interests)
                    }

                    if (user.flatProperties.languages.isNullOrEmpty())
                        holder.llVpLanguage.visibility = View.GONE
                    else {
                        holder.llVpLanguage.visibility = View.VISIBLE
                        holder.txtLanguage.text =
                            TextUtils.join(", ", user.flatProperties.languages)
                    }

                    if (CommonMethod.isDealBreakerEmpty(user.flatProperties.dealBreakers)) {
                        holder.llVpDeals.visibility = View.GONE
                    } else {
                        holder.llVpDeals.visibility = View.VISIBLE
                        val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
                        dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
                        dealBreakerView.show()
                    }

                    for (i in 0 until holder.llVp1Scroll.childCount step 2) {
                        if (i == 0)
                            (holder.llVp1Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp1Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
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

                    for (i in 0 until holder.llVp2Scroll.childCount step 2) {
                        if (i == 2)
                            (holder.llVp2Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp2Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
                }

                AdapterUserHolder.VP_SLIDE_IMAGES -> {
                    holder.llVp1.visibility = View.GONE
                    holder.llVp2.visibility = View.GONE
                    holder.llVp3.visibility = View.VISIBLE
                    var images = ArrayList<String>()


                    if (!user.dp.isNullOrEmpty())
                        images.add(user.dp!!)
                    if (!user.images.isNullOrEmpty()) {
                        images.addAll(user.images)
                    }
                    images = removeExtraImagesFromList(images)


                    holder.includeExploreImage.llExplorePhotoContainer1.visibility = View.GONE
                    holder.includeExploreImage.llExplorePhotoContainer2.visibility = View.GONE
                    holder.includeExploreImage.llExplorePhotoContainer3.visibility = View.GONE
                    when (images.size) {
                        1 -> {
                            holder.includeExploreImage.llExplorePhotoContainer1.visibility =
                                View.VISIBLE
                            val ll = holder.includeExploreImage.llExploreImageHolder
                            ll.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    ll.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val height = ll.height
                                    val width = ll.width
                                    val params = if (width < height)
                                        LinearLayout.LayoutParams(width, width)
                                    else LinearLayout.LayoutParams(height, height)
                                    holder.includeExploreImage.llExplorePhotoContainer1.layoutParams =
                                        params

                                }
                            })
                            val img =
                                ((holder.includeExploreImage.llExplorePhotoContainer1.getChildAt(0)
                                        as MaterialCardView).getChildAt(0)
                                        as ImageView)
                            ImageHelper.loadImage(
                                activity,
                                0,
                                img,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                        }

                        2 -> {
                            holder.includeExploreImage.llExplorePhotoContainer2.visibility =
                                View.VISIBLE
                            val card1 =
                                holder.includeExploreImage.llExplorePhotoContainer2.getChildAt(0) as MaterialCardView
                            val card2 =
                                holder.includeExploreImage.llExplorePhotoContainer2.getChildAt(2) as MaterialCardView
                            card1.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    card1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val width = card1.width
                                    val height = (width / 9) * 12
                                    card1.layoutParams.height = height
                                    card2.layoutParams.height = height
                                    CommonMethod.makeLog("Width", "" + width + "/" + height)
                                }
                            })
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card1.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card2.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[1]
                            )
                        }

                        3 -> {
                            holder.includeExploreImage.llExplorePhotoContainer3.visibility =
                                View.VISIBLE
                            val card1 =
                                (holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(0) as LinearLayout).getChildAt(
                                    0
                                ) as MaterialCardView
                            val card2 =
                                (holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(0) as LinearLayout).getChildAt(
                                    2
                                ) as MaterialCardView
                            val card3 =
                                holder.includeExploreImage.llExplorePhotoContainer3.getChildAt(2) as MaterialCardView
                            card1.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    card1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    val height = card1.height
                                    val width = ((height / 16) * 9)
                                    val params = LinearLayout.LayoutParams(width, height)
//                                card1.layoutParams = params
//                                card2.layoutParams = params
                                    card3.layoutParams = LinearLayout.LayoutParams(
                                        width,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1f
                                    )
                                    CommonMethod.makeLog("Width", "" + width + "/" + height)
                                }
                            })
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card1.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[0]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card2.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[1]
                            )
                            ImageHelper.loadImage(
                                activity,
                                0,
                                card3.getChildAt(0) as ImageView,
                                UrlConstants.IMAGE_URL + images[2]
                            )
                        }

                        /*4 -> {

                        }*/
                    }

                    for (i in 0 until holder.llVp3Scroll.childCount step 2) {
                        if (i == 4)
                            (holder.llVp3Scroll.getChildAt(i) as View).setBackgroundColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                )
                            )
                        else (holder.llVp3Scroll.getChildAt(i) as View).setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.blue_light
                            )
                        )
                    }
                }

            }
        }
    }

    private fun removeExtraImagesFromList(
        images: ArrayList<String>
    ): ArrayList<String> {
        if (images.size > 3) {
            images.removeAt(images.size - 1)
            removeExtraImagesFromList(images)
        }
        return images
    }

    private fun isLocationEmpty(location: ModelLocation?): Boolean {
        return (location?.loc == null || location.loc.coordinates.isNullOrEmpty())
    }
}
package com.joinflatshare.ui.explore.holder

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserHolder {

    fun bindUser(
        activity: ExploreActivity,
        user: User, holder: ItemExploreBinding
    ) {

        ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

        // Name
        holder.txtName.text =
            "${user.name?.firstName} ${user.name?.lastName}, ${CommonMethod.getAge(user.dob)}"

        // Distance
        holder.llDistance.visibility = View.GONE
        val locationLoggedInUser = AppConstants.loggedInUser?.flatProperties?.preferredLocation
        val locationUser = user.flatProperties.preferredLocation
        if (!(CommonMethod.isLocationEmpty(locationLoggedInUser)
                    || CommonMethod.isLocationEmpty(locationLoggedInUser))
        ) {
            val distance = (DistanceCalculator.calculateDistance(
                locationUser[0].loc.coordinates[1],
                locationUser[0].loc.coordinates[0],
                locationLoggedInUser!![0].loc.coordinates[1],
                locationLoggedInUser[0].loc.coordinates[0]
            ))
            if (!TextUtils.equals(distance, "NA")) {
                holder.llDistance.visibility = View.VISIBLE
                holder.txtDistance.text = distance + " away"
            }
        }

        holder.llExploreHolder.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra("phone", user.id)
            CommonMethod.switchActivity(activity, intent, false)
        }

        AdapterUserVpHolder.bindVp(activity, holder.includeExploreVp, user)
    }

    fun bindUser(
        activity: ChecksActivity,
        details: UserRecommendationItem, position: Int, holder: ItemChecksBinding
    ) {
        val user = details.data!!

        ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

        // Name
        holder.txtName.text =
            "${user.name?.firstName} ${user.name?.lastName}, ${CommonMethod.getAge(user.dob)}"

        // Distance
        holder.llDistance.visibility = View.GONE
        if (!(CommonMethod.isLocationEmpty(AppConstants.loggedInUser?.location) || CommonMethod.isLocationEmpty(
                user.location
            ))
        ) {
            val distance = (DistanceCalculator.calculateDistance(
                user.location.loc.coordinates[1],
                user.location.loc.coordinates[0],
                AppConstants.loggedInUser?.location?.loc?.coordinates!![1],
                AppConstants.loggedInUser?.location?.loc?.coordinates!![0]
            ))
            if (!TextUtils.equals(distance, "NA")) {
                holder.llDistance.visibility = View.VISIBLE
                holder.txtDistance.text = distance + " away"
            }
        }

        holder.llChecksHolder.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra("phone", user.id)
            CommonMethod.switchActivity(activity, intent, false)
        }

        AdapterUserVpHolder.bindVp(activity, holder.includeExploreVp, user)

        if (activity.mode == ChecksActivity.MODE_CHECKS) {
            holder.llExploreSuperCheck.visibility = View.VISIBLE
            holder.llExploreButtons.visibility = View.GONE
            if (details.details.chatRequestSent) {
                holder.txtChecksButton.text = "Already Sent"
                holder.llExploreSuperCheck.setOnClickListener { }
            } else {
                holder.txtChecksButton.text = "Send SuperCheck"
                holder.llExploreSuperCheck.setOnClickListener {
                    WebserviceManager().sendChatRequest(
                        activity,
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                        user.id,
                        object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                            override fun onResponseCallBack(response: String) {
                                details.details.chatRequestSent = true
                                bindUser(activity, details, position, holder)
                            }
                        })
                }
            }
        } else {
            holder.llExploreSuperCheck.visibility = View.GONE
            holder.llExploreButtons.visibility = View.VISIBLE
            holder.imgBlock.setOnClickListener {
                // TODO implement block
            }
            holder.imgReject.setOnClickListener {
                WebserviceManager().sendChatRequestResponse(
                    activity,
                    false,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    user.id,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            activity.dataBinder.adapter.removeItem(position)
                        }
                    })
            }
            holder.imgAccept.setOnClickListener {
                WebserviceManager().sendChatRequestResponse(
                    activity,
                    true,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    user.id,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            val resp: BaseResponse? =
                                Gson().fromJson(response, BaseResponse::class.java)
                            if (resp?.matched == true) {
                                // TODO show match popup
                            }
                            activity.dataBinder.adapter.removeItem(position)
                        }
                    })
            }
        }
    }

    fun showConnectionMatch(
        searchType: String,
        activity: BaseActivity,
        user: User,
        flatId: String?
    ) {
        if (searchType == BaseActivity.TYPE_FHT) {
            DialogConnection(
                activity,
                AppConstants.loggedInUser, null, user,
                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
            )
        } else if (searchType == BaseActivity.TYPE_DATE_CASUAL
            || searchType == BaseActivity.TYPE_DATE_LONG_TERM
            || searchType == BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS
        ) {
            var dateType = ""
            when (searchType) {
                BaseActivity.TYPE_DATE_CASUAL -> dateType =
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL

                BaseActivity.TYPE_DATE_LONG_TERM -> dateType =
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM

                BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS -> dateType =
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS

            }
            DialogConnection(
                activity,
                AppConstants.loggedInUser, null, user,
                dateType
            )
        } else {
            DialogConnection(
                activity,
                user,
                flatId, null,
                ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
            )
        }
    }
}
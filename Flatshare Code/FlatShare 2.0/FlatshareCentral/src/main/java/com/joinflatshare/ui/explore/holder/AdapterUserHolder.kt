package com.joinflatshare.ui.explore.holder

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.bottomsheet.EliteLearnMoreBottomSheet
import com.joinflatshare.ui.bottomsheet.IncompleteProfileBottomSheet
import com.joinflatshare.ui.bottomsheet.MatchBottomSheet
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.explore.ExploreAdapter
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by debopam on 14/11/22
 */
class AdapterUserHolder {

    fun bindUser(
        adapter: ExploreAdapter,
        details: UserRecommendationItem, holder: ItemExploreBinding, position: Int
    ) {
        val user = details.data!!
        val activity = adapter.activity

        ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

        // Name & DOB
        val name = "${user.name?.firstName} ${user.name?.lastName}"
        val dob = CommonMethod.getAge(user.dob)
        holder.txtName.text = if (dob.isEmpty()) name else "$name, $dob"

        // Elite
        holder.imgElite.visibility = if (CommonMethod.isEliteMember(user)) View.VISIBLE else View.GONE

        // Distance
        holder.llDistance.visibility = View.GONE
        val locationLoggedInUser = AppConstants.loggedInUser?.flatProperties?.preferredLocation
        val locationUser = user.flatProperties.preferredLocation
        if (!(CommonMethod.isLocationEmpty(locationLoggedInUser)
                    || CommonMethod.isLocationEmpty(locationUser))
        ) {
            val distance = (DistanceCalculator.calculateDistance(
                user, AppConstants.loggedInUser!!
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

        holder.llExploreReject.setOnClickListener {
            val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
            if (completion == false) {
                IncompleteProfileBottomSheet(
                    activity
                ) { }
                return@setOnClickListener
            }
            val rejectLikeUrl = WebserviceCustomRequestHandler.getRejectLikeRequest(
                BaseActivity.TYPE_FHT, ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT, user.id
            )
            WebserviceManager().rejectLike(activity, rejectLikeUrl,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        MixpanelUtils.onButtonClicked("Feed Reject")
                        adapter.removeItem(position)
                    }
                })
        }

        holder.llExploreChatRequest.setOnClickListener {
            val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
            if (completion == false) {
                IncompleteProfileBottomSheet(
                    activity
                ) {}
                return@setOnClickListener
            }
            WebserviceManager().sendChatRequest(
                activity,
                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT, user.id,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        MixpanelUtils.onButtonClicked("Feed SuperCheck")
                        adapter.removeItem(position)
                    }

                    override fun onCallBackPayment(count: Int) {
                        PaymentHandler.showPaymentForChats(activity,null)
                    }
                })
        }

        holder.imgElite.setOnClickListener {
            if (!CommonMethod.isEliteMember(AppConstants.loggedInUser!!))
                EliteLearnMoreBottomSheet(activity, user)
        }

        AdapterUserVpHolder.bindVp(activity, holder.includeExploreVp, user)
    }

    fun bindUser(
        activity: ChecksActivity,
        details: UserRecommendationItem, position: Int, holder: ItemChecksBinding
    ) {
        val user = details.data!!

        ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

        // Name & DOB
        val name = "${user.name?.firstName} ${user.name?.lastName}"
        val dob = CommonMethod.getAge(user.dob)
        holder.txtName.text = if (dob.isEmpty()) name else "$name, $dob"

        // Elite
        holder.imgElite.visibility = if (CommonMethod.isEliteMember(user)) View.VISIBLE else View.GONE

        // Distance
        holder.llDistance.visibility = View.GONE
        if (!(CommonMethod.isLocationEmpty(AppConstants.loggedInUser?.flatProperties?.preferredLocation)
                    || CommonMethod.isLocationEmpty(
                user.flatProperties.preferredLocation
            ))
        ) {
            val distance = (DistanceCalculator.calculateDistance(
                user, AppConstants.loggedInUser!!
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
                                MixpanelUtils.onButtonClicked("Send SuperCheck")
                                details.details.chatRequestSent = true
                                bindUser(activity, details, position, holder)
                            }

                            override fun onCallBackPayment(count: Int) {
                                PaymentHandler.showPaymentForChats(activity,null)
                            }
                        })
                }
            }
        } else {
            holder.llExploreSuperCheck.visibility = View.GONE
            holder.llExploreButtons.visibility = View.VISIBLE
            holder.llCheckReject.setOnClickListener {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion == false) {
                    IncompleteProfileBottomSheet(
                        activity, null
                    )
                    return@setOnClickListener
                }
                WebserviceManager().sendChatRequestResponse(
                    activity,
                    false,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    user.id,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            MixpanelUtils.onButtonClicked("Check Rejected")
                            activity.dataBinder.adapter.removeItem(position)
                        }
                    })
            }
            holder.llCheckAccept.setOnClickListener {
                val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
                if (completion == false) {
                    IncompleteProfileBottomSheet(
                        activity, null
                    )
                    return@setOnClickListener
                }
                WebserviceManager().sendChatRequestResponse(
                    activity,
                    true,
                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                    user.id,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            MixpanelUtils.onButtonClicked("Check Accepted")
                            MixpanelUtils.onMatched(user.id, BaseActivity.TYPE_FHT)
                            showConnectionMatch(BaseActivity.TYPE_FHT, activity, user)
                            activity.dataBinder.adapter.removeItem(position)
                        }
                    })
            }
        }
        holder.imgElite.setOnClickListener {
            if (!CommonMethod.isEliteMember(AppConstants.loggedInUser!!))
                EliteLearnMoreBottomSheet(activity, user)
        }
    }

    fun showConnectionMatch(
        searchType: String,
        activity: BaseActivity,
        user: User
    ) {
        if (searchType == BaseActivity.TYPE_FHT) {
            MatchBottomSheet(activity, AppConstants.loggedInUser, user)
        }
    }
}
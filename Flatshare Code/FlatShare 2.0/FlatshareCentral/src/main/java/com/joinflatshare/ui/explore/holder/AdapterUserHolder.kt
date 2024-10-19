package com.joinflatshare.ui.explore.holder

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.bottomsheet.EliteLearnMoreBottomSheet
import com.joinflatshare.ui.bottomsheet.IncompleteProfileBottomSheet
import com.joinflatshare.ui.bottomsheet.MatchBottomSheet
import com.joinflatshare.ui.bottomsheet.VerifiedBottomSheet
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.connection.ConnectionListActivity
import com.joinflatshare.ui.explore.ExploreActivity
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


/**
 * Created by debopam on 14/11/22
 */
class AdapterUserHolder {

    fun bindUser(
        adapter: ExploreAdapter, position: Int, holder: ItemExploreBinding,
        details: UserRecommendationItem
    ) {
        val user = details.data!!
        val activity = adapter.activity

        // Image
        ImageHelper.loadProfileImage(activity, holder.imgProfile, holder.txtPhoto, user)

        // Name & DOB
        val name = "${user.name?.firstName} ${user.name?.lastName}"
        val dob = CommonMethod.getAge(user.dob)
        holder.txtName.text = if (dob.isEmpty()) name else "$name, $dob"

        // Verified
        holder.imgVerified.visibility =
            if (user.verification?.isVerified == true) View.VISIBLE else View.GONE

        // Elite
        holder.imgElite.visibility =
            if (CommonMethod.isEliteMember(user)) View.VISIBLE else View.GONE

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

        holder.imgElite.setOnClickListener {
            if (!CommonMethod.isEliteMember(AppConstants.loggedInUser))
                EliteLearnMoreBottomSheet(activity, user)
        }

        holder.llButtonsChecks.visibility = View.GONE
        holder.llButtonsExplore.visibility = View.GONE
        holder.llButtonConnection.visibility = View.GONE
        if (activity is ExploreActivity) {
            holder.llButtonsExplore.visibility = View.VISIBLE
        } else if (activity is ChecksActivity) {
            holder.llButtonsChecks.visibility = View.VISIBLE

        } else if (activity is ConnectionListActivity) {
            holder.llButtonConnection.visibility = View.VISIBLE
        }

        AdapterUserVpHolder.bindVp(activity, holder.includeExploreVp, user)
        applyAdapterClicks(holder, adapter, details, position)

    }

    private fun applyAdapterClicks(
        holder: ItemExploreBinding,
        adapter: ExploreAdapter,
        details: UserRecommendationItem,
        position: Int
    ) {
        val user = details.data!!
        val activity = adapter.activity
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
                        MixpanelUtils.onChatRequestRejected(user.id)
                        adapter.removeItem(position)
                    }
                })
        }

        holder.llExploreChatRequest.setOnClickListener {
            // Profile Complete
            val completion = AppConstants.loggedInUser?.completed?.isConsideredCompleted
            if (completion == false) {
                IncompleteProfileBottomSheet(activity, null)
                return@setOnClickListener
            }
            // User verified
            val isVerified = AppConstants.loggedInUser?.verification?.isVerified
            if (isVerified == false) {
                VerifiedBottomSheet(activity, null)
                return@setOnClickListener
            }
            WebserviceManager().sendChatRequest(
                activity,
                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT, user.id,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        MixpanelUtils.onChatRequested(
                            user.id,
                            BaseActivity.TYPE_FHT
                        )
                        adapter.removeItem(position)
                    }

                    override fun onCallBackPayment(count: Int) {
                        PaymentHandler.showPaymentForChats(activity, null)
                    }
                })
        }

        holder.llButtonConnection.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", user.id, null))
            CommonMethod.switchActivity(activity, intent, false)
        }

        if (activity is ChecksActivity) {
            if (activity.mode == ChecksActivity.MODE_CHECKS) {
                holder.llButtonSuperCheck.visibility = View.VISIBLE
                if (details.details.chatRequestSent) {
                    holder.txtChecksButton.text = "Already Sent"
                    holder.llButtonSuperCheck.setOnClickListener { }
                } else {
                    holder.txtChecksButton.text = "Send SuperCheck"
                    holder.llButtonSuperCheck.setOnClickListener {
                        WebserviceManager().sendChatRequest(
                            activity,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                            user.id,
                            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                                override fun onResponseCallBack(response: String) {
                                    MixpanelUtils.onChatRequested(
                                        user.id,
                                        BaseActivity.TYPE_FHT
                                    )
                                    details.details.chatRequestSent = true
                                    bindUser(adapter, position, holder, details)
                                }

                                override fun onCallBackPayment(count: Int) {
                                    PaymentHandler.showPaymentForChats(activity, null)
                                }
                            })
                    }
                }
            } else {
                holder.llButtonSuperCheck.visibility = View.GONE
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
                                MixpanelUtils.onChatRequestRejected(user.id)
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
                                MixpanelUtils.onChatRequestAccepted(user.id)
                                MixpanelUtils.onMatched(user.id, BaseActivity.TYPE_FHT)
                                showConnectionMatch(BaseActivity.TYPE_FHT, activity, user)
                                activity.dataBinder.adapter.removeItem(position)
                            }
                        })
                }
            }
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
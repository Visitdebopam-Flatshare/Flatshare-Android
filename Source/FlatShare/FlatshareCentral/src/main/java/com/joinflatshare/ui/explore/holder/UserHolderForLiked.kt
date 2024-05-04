package com.joinflatshare.ui.explore.holder

import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.explore.Details
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.ui.liked.LikedActivity
import com.joinflatshare.utils.helper.DistanceCalculator
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 26/06/23
 */
object UserHolderForLiked {
    fun setLocation(
        searchType: String,
        details: Details,
        holder: ItemExploreBinding,
        flat: MyFlatData?
    ) {
        if (searchType.equals(BaseActivity.TYPE_FHT)) {
            if (details.coordinates.isEmpty()) holder.txtUserLocation.text =
                "NA"
            else holder.txtUserLocation.text = DistanceCalculator.calculateDistance(
                details.coordinates[0],
                details.coordinates[1],
                AppConstants.loggedInUser?.location?.loc?.coordinates!![0],
                AppConstants.loggedInUser?.location?.loc?.coordinates!![1]
            )
        } else {
            if (details.coordinates.isEmpty() || flat?.flatProperties?.location?.loc?.coordinates.isNullOrEmpty()) holder.txtUserLocation.text =
                "NA"
            else holder.txtUserLocation.text = DistanceCalculator.calculateDistance(
                details.coordinates[0],
                details.coordinates[1],
                flat?.flatProperties?.location?.loc?.coordinates!![0],
                flat.flatProperties.location?.loc?.coordinates!![1]
            )
        }
    }


    fun chatClicked(
        activity: LikedActivity,
        position: Int,
        connectionType: String,
        data: UserRecommendationItem,
        holder: ItemExploreBinding,
        flat: MyFlatData?,
        adapter: AdapterUserHolder,
        searchType: String,
        mixpanelSearchType: String
    ) {
        val item = data.data
        activity.viewBinding.rvLiked.smoothScrollToPosition(position)
        activity.viewBinding.rvLiked.addOnItemTouchListener(activity.rvScrollStopListener)
        activity.apiManager.sendConnectionRequest(
            connectionType, item.id
        ) { response ->
            activity.viewBinding.rvLiked.removeOnItemTouchListener(activity.rvScrollStopListener)
            if (adapter.isRestricted(response)) {
                // Checking payment gateway
                PaymentHandler.showPaymentForChats(activity, null)
            } else {
                DialogLottieViewer.loadAnimation(
                    holder.lottieLike, R.raw.lottie_chat_request
                ) {
                    activity.viewBinding.rvLiked.removeOnItemTouchListener(activity.rvScrollStopListener)
                }
                val resp = response as com.joinflatshare.pojo.BaseResponse
                if (resp.status == 200) {
                    if (searchType == BaseActivity.TYPE_USER)
                        MixpanelUtils.onChatRequested(
                            flat!!.mongoId, item.id, mixpanelSearchType
                        )
                    else MixpanelUtils.onChatRequested(
                        AppConstants.loggedInUser!!.id, item.id, mixpanelSearchType
                    )
                    // Check if the connection is accepted, meaning  the other person had
                    // already sent a connection request.
                    if (resp.message.equals("Accepted.")) {
                        // Show a Match pop for connection
                        adapter.showConnectionMatch(
                            searchType,
                            activity,
                            item,
                            flat!!.mongoId
                        )
                        activity.users.removeAt(position)
                        activity.apiController.adapter?.notifyDataSetChanged()
                    } else {
                        data.details.chatRequestSent = true
                        activity.users[position] = data
                        activity.apiController.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
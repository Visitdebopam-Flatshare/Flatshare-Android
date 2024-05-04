package com.joinflatshare.ui.explore.holder

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.customviews.inapp_review.InAppReview
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.explore.FlatRecommendationItem
import com.joinflatshare.pojo.likes.LikeRequest
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.base.BaseActivity.TYPE_FLAT
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.dialogs.DialogIncompleteProfile
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.ui.dialogs.report.DialogReport
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.ui.explore.adapter.ExploreFlatVpAdapter
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.flat.details.FlatDetailsImageAdapter
import com.joinflatshare.ui.liked.LikedActivity
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class AdapterFlatHolder(private val holder: ItemExploreBinding) :
    RecyclerView.ViewHolder(holder.root) {

    fun bindFlat(
        activity: BaseActivity,
        position: Int
    ) {
        var data = FlatRecommendationItem()
        if (activity is ExploreActivity)
            data = activity.flatData[position]
        else if (activity is LikedActivity)
            data = activity.flats[position]
        val item = data.flat
        holder.llExploreNameFlat.visibility = View.VISIBLE
        holder.llExploreNameUser.visibility = View.GONE

        // Room Type
        if (!item.flatProperties.roomType.isNullOrBlank()) {
            holder.txtDot1.visibility = View.VISIBLE
            holder.txtRoomtype.visibility = View.VISIBLE
            holder.txtRoomtype.text = item.flatProperties.roomType
        } else {
            holder.txtDot1.visibility = View.GONE
            holder.txtRoomtype.visibility = View.GONE
        }

        // Room Size
        if (!item.flatProperties.flatsize.isNullOrBlank()) {
            holder.txtDot2.visibility = View.VISIBLE
            holder.txtRoomsize.visibility = View.VISIBLE
            holder.txtRoomsize.text = item.flatProperties.flatsize
        } else {
            holder.txtDot2.visibility = View.GONE
            holder.txtRoomsize.visibility = View.GONE
        }

        // Score
        var score = item.score
        if (score < 0) score = 0
        holder.txtFscoreFlat.text = "" + score

        if ((activity is ExploreActivity) ||
            ((activity is LikedActivity) && data.details.revealed)
        ) {
            holder.rvExploreImages.visibility = View.VISIBLE
            holder.frameVp.visibility = View.VISIBLE
            holder.llExploreLock.visibility = View.GONE
            holder.txtChatRequest.visibility = View.VISIBLE
            holder.imgOption.visibility = View.VISIBLE
            holder.txtReveal.visibility = View.GONE

            // images
            if (item.images.isNullOrEmpty()) {
                holder.rvExploreImages.visibility = View.GONE
            } else {
                holder.rvExploreImages.visibility = View.VISIBLE
                holder.rvExploreImages.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                item.images.reverse()
                holder.rvExploreImages.adapter = FlatDetailsImageAdapter(
                    activity, item.images
                ) { _, _ ->
                    onHolderClick(activity, position, data)
                }
            }


            // Viewpager
            val vpSlide = ArrayList<Int>()

            // Flat Details
            vpSlide.add(ExploreAdapter.VP_SLIDE_FLATDETAILS)

            // Flat mates
            if (item.flatMates != null && item.flatMates.size > 0)
                vpSlide.add(ExploreAdapter.VP_SLIDE_FLATMATES)

            val properties = item.flatProperties
            // Interests
            if (!properties.interests.isNullOrEmpty())
                vpSlide.add(ExploreAdapter.VP_SLIDE_INTEREST)

            // Deal Breakers
            if (!DealBreakerView.areAllDealBreakersEmpty(properties.dealBreakers))
                vpSlide.add(ExploreAdapter.VP_SLIDE_DEAL_BREAKERS)

            // Languages
            if (!properties.languages.isNullOrEmpty())
                vpSlide.add(ExploreAdapter.VP_SLIDE_LANGUAGES)

            // Norms and expenses
            if (!item.norms.isNullOrBlank())
                vpSlide.add(ExploreAdapter.VP_SLIDE_NORMS)

            if (vpSlide.size == 0) {
                holder.frameVp.visibility = View.GONE
            } else {
                holder.frameVp.visibility = View.VISIBLE
                holder.vpExplore.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                holder.vpExplore.adapter =
                    ExploreFlatVpAdapter(
                        activity,
                        vpSlide,
                        data,
                        item
                    ) { intent, _ ->
                        handleCallback(activity, intent, position, data)
                    }

                // tab layout
                TabLayoutMediator(holder.tabLayout, holder.vpExplore) { tab, tabPosition ->
                }.attach()
            }

            val details = data.details
            // Chat Request
            if (details.chatRequestSent) {
                holder.txtChatRequest.text = "Chat Request Sent"
                holder.txtChatRequest.alpha = 0.7f
                holder.txtChatRequest.setOnClickListener {

                }
            } else {
                holder.txtChatRequest.text = "Chat Request"
                holder.txtChatRequest.alpha = 1f
                holder.txtChatRequest.setOnClickListener {
                    if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                        DialogIncompleteProfile(activity, TYPE_FLAT)
                    } else {
                        if (activity is ExploreActivity) {
                            activity.viewBind.rvExplore.smoothScrollToPosition(position)
                            activity.viewBind.rvExplore.addOnItemTouchListener(activity.binder.rvScrollStopListener)
                        } else if (activity is LikedActivity) {
                            activity.viewBinding.rvLiked.smoothScrollToPosition(position)
                            activity.viewBinding.rvLiked.addOnItemTouchListener(activity.rvScrollStopListener)
                        }
                        activity.apiManager.sendConnectionRequest(
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F, item.id
                        ) { response ->
                            if (activity is ExploreActivity) {
                                activity.viewBind.rvExplore.removeOnItemTouchListener(activity.binder.rvScrollStopListener)
                            } else if (activity is LikedActivity) {
                                activity.viewBinding.rvLiked.removeOnItemTouchListener(activity.rvScrollStopListener)
                            }
                            if (isRestricted(response)) {
                                // Checking payment gateway
                                PaymentHandler.showPaymentForChats(activity, null)
                            } else {
                                if (activity is ExploreActivity) {
                                    DialogLottieViewer.loadAnimation(
                                        holder.lottieLike, R.raw.lottie_chat_request
                                    ) {
                                        activity.viewBind.rvExplore.removeOnItemTouchListener(
                                            activity.binder.rvScrollStopListener
                                        )
                                    }
                                } else if (activity is LikedActivity) {
                                    DialogLottieViewer.loadAnimation(
                                        holder.lottieLike, R.raw.lottie_chat_request
                                    ) {
                                        activity.viewBinding.rvLiked.removeOnItemTouchListener(
                                            activity.rvScrollStopListener
                                        )
                                    }
                                }
                                val resp = response as com.joinflatshare.pojo.BaseResponse
                                if (resp.status == 200) {
                                    MixpanelUtils.onChatRequested(
                                        AppConstants.loggedInUser?.id!!,
                                        item.id,
                                        "Shared Flat Search"
                                    )
                                    // Check if the connection is accepted, meaning  the other person had already sent a connection request.
                                    if (resp.message.equals("Accepted.")) {
                                        // Show a Match pop for connection
                                        DialogConnection(
                                            activity,
                                            AppConstants.loggedInUser,
                                            item.mongoId,
                                            null,
                                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                                        )
                                        if (activity is ExploreActivity) {
                                            activity.flatData.removeAt(position)
                                            activity.binder.adapter.notifyDataSetChanged()
                                        } else if (activity is LikedActivity) {
                                            activity.flats.removeAt(position)
                                            activity.apiController.adapter?.notifyDataSetChanged()
                                        }
                                    } else {
                                        data.details.chatRequestSent = true
                                        if (activity is ExploreActivity) {
                                            activity.flatData[position] = data
                                            activity.binder.adapter.notifyDataSetChanged()
                                        } else if (activity is LikedActivity) {
                                            activity.flats[position] = data
                                            activity.apiController.adapter?.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Like Button
            if (activity is ExploreActivity) {
                if (data.details.isLiked) {
                    holder.cardLike.visibility = View.GONE
                    holder.cardNotInterested.visibility = View.GONE
                    /*holder.cardLike.setOnClickListener {
                        activity.apiManager.exploreDisLike(
                            false,
                            TYPE_FLAT, ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F, item.mongoId
                        ) { response ->
                            val resp = response as BaseResponse
                            if (resp.status == 200) {
                                data.details.liked = false
                                activity.flatData.set(position, data)
                                activity.binder.adapter.notifyItemChanged(position)

                                val channel = SendBirdFlatChannel(activity)
                                channel.removeFlatMember(AppConstants.loggedInUser?.id!!, item.mongoId)
                            }
                        }
                    }*/
                } else {
                    holder.cardLike.visibility = View.VISIBLE
                    holder.cardNotInterested.visibility = View.VISIBLE
                    holder.cardLike.setOnClickListener {
                        if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                            DialogIncompleteProfile(activity, TYPE_FLAT)
                        } else {
                            activity.viewBind.rvExplore.smoothScrollToPosition(position)
                            activity.viewBind.rvExplore.addOnItemTouchListener(activity.binder.rvScrollStopListener)
                            val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                                activity.SEARCH_TYPE,
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
                                item.mongoId
                            )
                            WebserviceManager().addLike(activity,
                                likeUrl,
                                LikeRequest(data.details.preferredLocation),
                                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                                    override fun onCallBackPayment(count: Int) {
                                        activity.viewBind.rvExplore.removeOnItemTouchListener(
                                            activity.binder.rvScrollStopListener
                                        )
                                        // Checking payment gateway
                                        PaymentHandler.showPaymentForChecks(activity, null)
                                    }

                                    override fun onResponseCallBack(response: String) {
                                        activity.viewBind.rvExplore.removeOnItemTouchListener(
                                            activity.binder.rvScrollStopListener
                                        )
                                        val resp =
                                            Gson().fromJson(response, BaseResponse::class.java)
                                        DialogLottieViewer.loadAnimation(
                                            holder.lottieLike, R.raw.lottie_like
                                        ) {
                                            activity.viewBind.rvExplore.removeOnItemTouchListener(
                                                activity.binder.rvScrollStopListener
                                            )
                                        }
                                        if (resp.status == 200) {
                                            MixpanelUtils.onLiked(item.id, "Shared Flat Search")
                                            if (resp.matched) {
                                                MixpanelUtils.onMatched(
                                                    item.id,
                                                    "Shared Flat Search"
                                                )
                                                DialogConnection(
                                                    activity,
                                                    AppConstants.loggedInUser,
                                                    item.mongoId,
                                                    null,
                                                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                                                )
                                                if (resp.shouldShowReview())
                                                    InAppReview.show(activity)
                                            }
                                            if (activity.binder.adapter.itemCount <= 1)
                                                activity.binder.showEmptyData("Sorry, we’ve run out of potential flats.")
                                            else {
                                                activity.flatData.removeAt(position)
                                                activity.binder.adapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                })
                        }
                    }
                }
            } else if (activity is LikedActivity) {
                if (activity.viewTypeReceivedLikes) {
                    if (details.isLiked)
                        holder.cardLike.visibility = View.GONE
                    else holder.cardLike.visibility = View.VISIBLE
                    holder.cardNotInterested.visibility = View.VISIBLE
                    holder.cardLike.setOnClickListener {
                        if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                            DialogIncompleteProfile(activity, TYPE_FLAT)
                        } else {
                            activity.viewBinding.rvLiked.smoothScrollToPosition(position)
                            activity.viewBinding.rvLiked.addOnItemTouchListener(activity.rvScrollStopListener)
                            val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                                activity.SEARCH_TYPE,
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
                                item.mongoId
                            )
                            WebserviceManager().addLike(activity,
                                likeUrl,
                                LikeRequest(data.details.preferredLocation),
                                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                                    override fun onCallBackPayment(count: Int) {
                                        activity.viewBinding.rvLiked.removeOnItemTouchListener(
                                            activity.rvScrollStopListener
                                        )
                                        // Checking payment gateway
                                        PaymentHandler.showPaymentForChecks(activity, null)
                                    }

                                    override fun onResponseCallBack(response: String) {
                                        activity.viewBinding.rvLiked.removeOnItemTouchListener(
                                            activity.rvScrollStopListener
                                        )
                                        val resp =
                                            Gson().fromJson(response, BaseResponse::class.java)
                                        DialogLottieViewer.loadAnimation(
                                            holder.lottieLike, R.raw.lottie_like
                                        ) {
                                            activity.viewBinding.rvLiked.removeOnItemTouchListener(
                                                activity.rvScrollStopListener
                                            )
                                        }
                                        if (resp.status == 200) {
                                            MixpanelUtils.onLiked(item.id, "Shared Flat Search")
                                            if (resp.matched) {
                                                MixpanelUtils.onMatched(
                                                    item.id,
                                                    "Shared Flat Search"
                                                )
                                                DialogConnection(
                                                    activity,
                                                    AppConstants.loggedInUser,
                                                    item.mongoId,
                                                    null,
                                                    ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                                                )
                                                if (resp.shouldShowReview())
                                                    InAppReview.show(activity)
                                            }
                                            if (activity.apiController.adapter!!.itemCount <= 1)
                                                activity.apiController.hideView()
                                            else {
                                                activity.flats.removeAt(position)
                                                activity.apiController.adapter?.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                })
                        }
                    }
                } else {
                    if (AppConstants.isAppLive) {
                        holder.cardLike.visibility = View.GONE
                        holder.cardNotInterested.visibility = View.GONE
                    } else {
                        holder.cardLike.visibility = View.VISIBLE
                        holder.cardNotInterested.visibility = View.VISIBLE
                        holder.imgLike.setImageResource(R.drawable.ic_heart)
                        holder.cardLike.setOnClickListener {
                            activity.apiManager.exploreDisLike(
                                false,
                                BaseActivity.TYPE_FLAT,
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
                                item.mongoId
                            ) { response ->
                                val resp = response as com.joinflatshare.pojo.BaseResponse
                                if (resp.status == 200) {
                                    holder.imgLike.setImageResource(R.drawable.ic_heart_grey)
                                    activity.flats.removeAt(position)
                                    activity.apiController.adapter?.notifyDataSetChanged()
                                    if (activity.flats.size == 0) {
                                        activity.apiController.hideView()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            holder.feedHolder.setOnClickListener {
                onHolderClick(activity, position, data)
            }

            holder.cardNotInterested.setOnClickListener {
                notInterested(activity, item.mongoId, position)
            }

            holder.imgOption.setOnClickListener {
                val list = ArrayList<ModelBottomSheet>()
                if (FlatShareMessageGenerator.isFlatDataAvailableToShare(item))
                    list.add(ModelBottomSheet("Copy Link", 2))
                list.add(ModelBottomSheet("Report", 3))
                list.add(ModelBottomSheet("Not Interested", 2))
                BottomSheetView(activity, list).show { _, position ->
                    when (list[position].name) {
                        "Copy Link" -> {
                            activity.apiManager.showProgress()
                            DeepLinkHandler.createFlatDynamicLink(
                                item
                            ) { text ->
                                activity.apiManager.hideProgress()
                                if (!text.isNullOrBlank()) {
                                    if (text != "0") {
                                        val shareMessage =
                                            FlatShareMessageGenerator.generateFlatShareMessage(item) + "\n\n" + text
                                        CommonMethods.copyToClipboard(activity, shareMessage)
                                    }
                                }
                            }
                        }

                        "Report" -> {
                            DialogReport(
                                activity, TYPE_FLAT, "", item
                            ) { text ->
                                if (text.equals("1")) {
                                    if (activity is ExploreActivity) {
                                        activity.flatData.removeAt(position)
                                        activity.binder.adapter.notifyDataSetChanged()
                                    } else if (activity is LikedActivity) {
                                        activity.flats.removeAt(position)
                                        activity.apiController.adapter?.notifyDataSetChanged()
                                    }
                                }
                            }
                        }

                        "Not Interested" -> {
                            notInterested(activity, item.mongoId, position)
                        }
                    }
                }
            }
        } else {
            holder.rvExploreImages.visibility = View.GONE
            holder.frameVp.visibility = View.GONE
            holder.llExploreLock.visibility = View.VISIBLE
            holder.cardNotInterested.visibility = View.GONE
            holder.cardLike.visibility = View.GONE
            holder.txtChatRequest.visibility = View.GONE
            holder.imgOption.visibility = View.INVISIBLE
            holder.txtReveal.visibility = View.VISIBLE
            holder.txtReveal.setOnClickListener {
                if (activity is LikedActivity) {
                    activity.apiManager.revealLikes(
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
                        item.mongoId
                    ) { response ->
                        if (response is Int) {
                            PaymentHandler.showPaymentForReveals(
                                activity
                            ) { text ->
                                if (text.equals("1")) {
                                    activity.apiManager.revealLikes(
                                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
                                        item.mongoId
                                    ) {
                                        DialogLottieViewer.loadAnimation(
                                            holder.lottieLike,
                                            R.raw.lottie_reveal,
                                            null
                                        )
                                        activity.flats[position].details.revealed = true
                                        activity.apiController.adapter?.notifyItemChanged(position)
                                    }
                                }
                            }
                        } else {
                            DialogLottieViewer.loadAnimation(
                                holder.lottieLike,
                                R.raw.lottie_reveal,
                                null
                            )
                            activity.flats[position].details.revealed = true
                            activity.apiController.adapter?.notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    private fun notInterested(activity: BaseActivity, id: String, position: Int) {
        DialogLottieViewer.loadAnimation(holder.lottieLike, R.raw.lottie_not_interested, null)
        activity.apiManager.report(
            false,
            TYPE_FLAT,
            0, id
        ) { response: Any? ->
            val resp = response as com.joinflatshare.pojo.BaseResponse?
            if (resp!!.status == 200) {
                if (activity is ExploreActivity) {
                    activity.flatData.removeAt(position)
                    activity.binder.adapter.notifyDataSetChanged()
                } else if (activity is LikedActivity) {
                    activity.flats.removeAt(position)
                    activity.apiController.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun onHolderClick(
        activity: BaseActivity,
        position: Int,
        data: FlatRecommendationItem
    ) {
        val item = data.flat
        val intent = Intent(activity, FlatDetailsActivity::class.java)
        intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, RouteConstants.ROUTE_CONSTANT_FEED)
        intent.putExtra("phone", item.mongoId)
        CommonMethod.switchActivity(
            activity,
            intent
        ) { result ->
            if (result?.resultCode == Activity.RESULT_OK) {
                handleCallback(activity, result.data, position, data)
            }
        }
    }

    private fun handleCallback(
        activity: BaseActivity,
        intent: Intent?,
        position: Int,
        data: FlatRecommendationItem
    ) {
        val like = intent?.getBooleanExtra("like", false)
        val chat = intent?.getBooleanExtra("chat", false)
        val report = intent?.getBooleanExtra("report", false)
        if (like == true || report == true) {
            if (activity is ExploreActivity) {
                if (activity.binder.adapter.itemCount <= 1)
                    activity.binder.showEmptyData("Sorry, we’ve run out of potential flats.")
                else {
                    activity.flatData.removeAt(position)
                    activity.binder.adapter.notifyDataSetChanged()
                }
            }
        } else if (chat == true) {
            data.details.chatRequestSent = true
            if (activity is ExploreActivity) {
                activity.flatData[position] = data
                activity.binder.adapter.notifyItemChanged(position)
            } else if (activity is LikedActivity) {
                activity.flats[position] = data
                activity.apiController.adapter?.notifyItemChanged(position)
            }
        }
    }

    private fun isRestricted(response: Any?): Boolean {
        return response is Int
    }
}
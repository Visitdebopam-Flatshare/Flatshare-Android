package com.joinflatshare.ui.explore.holder

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.adapter.ExploreUserVpAdapter
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

    companion object {
        const val VP_SLIDE_ABOUT = 1
        const val VP_SLIDE_WORK = 2
        const val VP_SLIDE_IMAGES = 3
        var pageNo = 0
    }

    fun bindUser(
        activity: ExploreActivity,
        user: User, holder: ItemExploreBinding
    ) {

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

        holder.frameExploreTop.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra("phone", user.id)
            CommonMethod.switchActivity(activity, intent, false)
        }

        /*holder.viewExploreLeft.setOnClickListener {
            if (pageNo != 0) {
                holder.vpExplore.currentItem = 0
            }
        }

        holder.viewExploreRight.setOnClickListener {
            if (pageNo != 2) {
                holder.vpExplore.currentItem = 1
            }
        }*/


        // Viewpager
        val vpSlide = ArrayList<Int>()

        vpSlide.add(VP_SLIDE_ABOUT)

        // Work
        if (!user.work.isNullOrBlank() || !user.college?.name.isNullOrEmpty()
            || !user.hometown?.name.isNullOrEmpty() || user.score != 0
        )
            vpSlide.add(VP_SLIDE_WORK)

        if (vpSlide.size == 0) {
            holder.vpExplore.visibility = View.GONE
        } else {
            holder.vpExplore.visibility = View.VISIBLE
            holder.vpExplore.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            holder.vpExplore.adapter = ExploreUserVpAdapter(
                activity, vpSlide, user
            )
            holder.vpExplore.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 0) pageNo = 0 else pageNo = 2
                    onVpPageChange(activity, holder)
                }
            })
        }
    }

    private fun isLocationEmpty(location: ModelLocation?): Boolean {
        return (location?.loc == null || location.loc.coordinates.isNullOrEmpty())
    }

    private fun onVpPageChange(activity: ExploreActivity, holder: ItemExploreBinding) {
        for (i in 0 until holder.llVpScroll.childCount step 2) {
            if (i == pageNo)
                (holder.llVpScroll.getChildAt(i) as View).setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.white
                    )
                )
            else (holder.llVpScroll.getChildAt(i) as View).setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.blue_light
                )
            )
        }
    }

    fun bindUser(
        activity: ChecksActivity,
        details: UserRecommendationItem, position: Int, holder: ItemChecksBinding
    ) {
        val user = details.data

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

        holder.frameExploreTop.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra("phone", user.id)
            CommonMethod.switchActivity(activity, intent, false)
        }

        // Viewpager
        val vpSlide = ArrayList<Int>()

        vpSlide.add(VP_SLIDE_ABOUT)

        if (vpSlide.size == 0) {
            holder.vpExplore.visibility = View.GONE
        } else {
            holder.vpExplore.visibility = View.VISIBLE
            holder.vpExplore.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            holder.vpExplore.adapter = ExploreUserVpAdapter(
                activity, vpSlide, user
            )
        }

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
                    false,
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

    /*var data = UserRecommendationItem()
    var searchType = ""
    if (activity is ExploreActivity) {
        data = activity.userData[position]
        searchType = activity.SEARCH_TYPE
    } else if (activity is LikedActivity) {
        searchType = activity.SEARCH_TYPE

        data = activity.users[position]
    }
    var connectionType = ""
    var mixpanelSearchType = ""
    if (searchType.equals(BaseActivity.TYPE_USER)) {
        connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
        mixpanelSearchType = "Flatmate Search"
    } else if (searchType.equals(BaseActivity.TYPE_FHT)) {
        connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
        mixpanelSearchType = "FHT Search"
    } else if (searchType.equals(BaseActivity.TYPE_DATE)
        || searchType.equals(BaseActivity.TYPE_DATE_CASUAL)
        || searchType.equals(BaseActivity.TYPE_DATE_LONG_TERM)
        || searchType.equals(BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS)
    ) {
        val dateType = AppConstants.loggedInUser?.dateProperties?.dateType
        connectionType = "" + dateType
        when (dateType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL -> {
                mixpanelSearchType = "Casual Date Search"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM -> {
                mixpanelSearchType = "Long Term Date Search"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                mixpanelSearchType = "Activity Partners Date Search"
            }
        }
    } else {
        when (searchType) {
            BaseActivity.TYPE_DATE_CASUAL -> {
                connectionType = "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                mixpanelSearchType = "Casual Date Search"
            }

            BaseActivity.TYPE_DATE_LONG_TERM -> {
                connectionType = "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                mixpanelSearchType = "Long Term Date Search"
            }

            BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS -> {
                connectionType =
                    "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                mixpanelSearchType = "Activity Partners Date Search"
            }
        }
    }
    val item = data.data

    holder.llExploreNameFlat.visibility = View.GONE
    holder.llExploreNameUser.visibility = View.VISIBLE
    // Image
    ImageHelper.loadImage(
        activity,
        R.drawable.ic_user,
        holder.imgProfile, ImageHelper.getProfileImageWithAws(item)
    )

    // first get the width and height of image recyclerview
    var name = item.name?.firstName + " " + item.name?.lastName
    if (name.length > ExploreAdapter.CHARACTER_LIMIT_IN_USER_NAME) {
        name = name.substring(0, ExploreAdapter.CHARACTER_LIMIT_IN_USER_NAME) + "..."
    }
    holder.txtUserName.text = name
    holder.imgProfileVerified.visibility =
        if (item.verification?.isVerified == true) View.VISIBLE else View.GONE

    // Score
    var score = item.score
    if (score < 0) score = 0
    holder.txtFscoreUser.text = "" + score

    // Location
    val details = data.details
    if (activity is LikedActivity) {
        UserHolderForLiked.setLocation(searchType, details, holder, flat)
    } else {
        holder.txtUserLocation.text = "${details.distance} km"
        *//*if (details.userLocationMatch.isEmpty() || details.flatLocation.isEmpty()) holder.txtUserLocation.text =
                "NA"
            else holder.txtUserLocation.text = DistanceCalculator.calculateDistance(
                details.userLocationMatch[0],
                details.userLocationMatch[1],
                details.flatLocation[0],
                details.flatLocation[1]
            )*//*
        }

        // Age
        val dob: String = item.dob
        if (dob.isNotEmpty()) {
            val bYear = dob.split("/")[0].toInt()
            val cYear = Calendar.getInstance()[Calendar.YEAR]
            val bMonth = dob.split("/")[1].toInt()
            val cMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            if (bMonth > cMonth) holder.txtUserAge.text = "" + (cYear - bYear - 1)
            else holder.txtUserAge.text = "" + (cYear - bYear)
        }

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
            holder.rvExploreImages.visibility = View.VISIBLE
            if (item.images.isEmpty()) {
                holder.rvExploreImages.visibility = View.GONE
            } else {
                holder.rvExploreImages.visibility = View.VISIBLE
                holder.rvExploreImages.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                val adapter = ProfileEditImageAdapter(
                    activity
                ) { _, _ ->
                    onHolderClick(activity, position, searchType, data)
                }
                holder.rvExploreImages.adapter = adapter
                item.images.reverse()
                adapter.items = item.images
            }

            // Viewpager
            val vpSlide = ArrayList<Int>()

            // About
            if (!item.status.isNullOrBlank()) vpSlide.add(ExploreAdapter.VP_SLIDE_ABOUT)

            // Work and college
            if (!item.work.isNullOrBlank() || (item.hometown != null && item.hometown!!.name.isNotBlank()) || (item.college != null && item.college!!.name.isNotBlank())) vpSlide.add(
                ExploreAdapter.VP_SLIDE_WORK
            )

            val properties = item.flatProperties
            // Interests
            if (!properties.interests.isNullOrEmpty()) vpSlide.add(ExploreAdapter.VP_SLIDE_INTEREST)

            // Deal Breakers
            if (!DealBreakerView.areAllDealBreakersEmpty(properties.dealBreakers))
                vpSlide.add(ExploreAdapter.VP_SLIDE_DEAL_BREAKERS)


            // Languages
            if (!properties.languages.isNullOrEmpty()) vpSlide.add(ExploreAdapter.VP_SLIDE_LANGUAGES)

            if (vpSlide.size == 0) {
                holder.frameVp.visibility = View.GONE
            } else {
                holder.frameVp.visibility = View.VISIBLE
                holder.vpExplore.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                holder.vpExplore.adapter = ExploreUserVpAdapter(
                    activity, vpSlide, item, flat
                ) { intent, _ ->
                    handleCallback(activity, intent, position, data)
                }

                // tab layout
                TabLayoutMediator(holder.tabLayout, holder.vpExplore) { tab, tabPosition ->
                }.attach()
            }

            // Chat Request
            if (details.chatRequestSent) {
                holder.txtChatRequest.text = "Chat Request Sent"
                holder.txtChatRequest.alpha = 0.7f
                holder.txtChatRequest.setOnClickListener {}
            } else {
                holder.txtChatRequest.text = "Chat Request"
                holder.txtChatRequest.alpha = 1f
                holder.txtChatRequest.setOnClickListener {
                    val myFlat =
                        FlatShareApplication.getDbInstance().userDao().getFlatData()
                    // Check if FHT or FMS
                    if (searchType == BaseActivity.TYPE_USER) {
                        if (myFlat == null || myFlat.completed < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                            DialogIncompleteProfile(activity, searchType)
                            return@setOnClickListener
                        }
                    } else {
                        if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                            DialogIncompleteProfile(activity, searchType)
                            return@setOnClickListener
                        }
                    }
                    if (activity is LikedActivity) {
                        UserHolderForLiked.chatClicked(
                            activity,
                            position,
                            connectionType,
                            data,
                            holder,
                            flat,
                            this,
                            searchType,
                            mixpanelSearchType
                        )
                    } else if (activity is ExploreActivity) {
                        activity.viewBind.rvExplore.smoothScrollToPosition(position)
                        activity.viewBind.rvExplore.addOnItemTouchListener(activity.binder.rvScrollStopListener)

                        activity.apiManager.sendConnectionRequest(
                            connectionType, item.id
                        ) { response ->
                            activity.viewBind.rvExplore.removeOnItemTouchListener(activity.binder.rvScrollStopListener)
                            if (isRestricted(response)) {
                                // Checking payment gateway
                                PaymentHandler.showPaymentForChats(activity, null)
                            } else {
                                DialogLottieViewer.loadAnimation(
                                    holder.lottieLike, R.raw.lottie_chat_request
                                ) {
                                    activity.viewBind.rvExplore.removeOnItemTouchListener(
                                        activity.binder.rvScrollStopListener
                                    )
                                }
                                val resp = response as com.joinflatshare.pojo.BaseResponse
                                if (resp.status == 200) {
                                    if (searchType == BaseActivity.TYPE_USER)
                                        MixpanelUtils.onChatRequested(
                                            myFlat!!.mongoId, item.id, mixpanelSearchType
                                        )
                                    else MixpanelUtils.onChatRequested(
                                        AppConstants.loggedInUser!!.id, item.id, mixpanelSearchType
                                    )
                                    // Check if the connection is accepted, meaning  the other person had
                                    // already sent a connection request.
                                    if (resp.message.equals("Accepted.")) {
                                        // Show a Match pop for connection
                                        showConnectionMatch(
                                            searchType,
                                            activity,
                                            item,
                                            flat!!.mongoId
                                        )
                                        if (activity.userData.size == 1) {
                                            activity.reloadFeed()
                                        } else {
                                            activity.userData.removeAt(position)
                                            activity.binder.adapter.notifyDataSetChanged()
                                        }
                                    } else {
                                        data.details.chatRequestSent = true
                                        activity.userData[position] = data
                                        activity.binder.adapter.notifyDataSetChanged()
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
                    *//*holder.cardLike.setOnClickListener {
                        activity.apiManager.exploreDisLike(
                            false,
                            BaseActivity.TYPE_USER,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U,
                            item.id
                        ) { response ->
                            val resp = response as BaseResponse
                            if (resp.status == 200) {
                                data.details.liked = false
                                activity.userData[position] = data
                                holder.cardLike.setImageResource(R.drawable.ic_heart_grey)

                                val channel = SendBirdFlatChannel(activity)
                                channel.removeFlatMember(item.id, flat?.mongoId!!)
                            }
                        }
                    }*//*
                } else {
                    holder.cardLike.visibility = View.VISIBLE
                    holder.cardNotInterested.visibility = View.VISIBLE
                    holder.cardLike.setOnClickListener {
                        // Check if FHT or FMS
                        if (searchType == BaseActivity.TYPE_USER) {
                            val myFlat =
                                FlatShareApplication.getDbInstance().userDao().getFlatData()
                            if (myFlat == null || myFlat.completed < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                                DialogIncompleteProfile(activity, searchType)
                                return@setOnClickListener
                            }
                        } else {
                            if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                                DialogIncompleteProfile(activity, searchType)
                                return@setOnClickListener
                            }
                        }
                        activity.viewBind.rvExplore.smoothScrollToPosition(position)
                        activity.viewBind.rvExplore.addOnItemTouchListener(activity.binder.rvScrollStopListener)
                        val locationList = ArrayList<Double>()
                        locationList.add(data.details.userLocationMatch[1])
                        locationList.add(data.details.userLocationMatch[0])
                        val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                            searchType, connectionType, item.id
                        )
                        WebserviceManager().addLike(activity,
                            likeUrl,
                            LikeRequest(data.details.preferredLocation),
                            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                                override fun onCallBackPayment(count: Int) {
                                    // Checking payment gateway
                                    PaymentHandler.showPaymentForChecks(activity, null)
                                }

                                override fun onResponseCallBack(response: String) {
                                    val resp = Gson().fromJson(response, BaseResponse::class.java)
                                    DialogLottieViewer.loadAnimation(
                                        holder.lottieLike, R.raw.lottie_like
                                    ) {
                                        activity.viewBind.rvExplore.removeOnItemTouchListener(
                                            activity.binder.rvScrollStopListener
                                        )
                                    }
                                    if (resp.status == 200) {
                                        MixpanelUtils.onLiked(item.id, mixpanelSearchType)
                                        if (resp.matched) {
                                            MixpanelUtils.onMatched(item.id, mixpanelSearchType)
                                            showConnectionMatch(
                                                searchType,
                                                activity,
                                                item,
                                                flat!!.mongoId
                                            )
                                            if (resp.shouldShowReview())
                                                InAppReview.show(activity)
                                        }
                                        if (activity.binder.adapter.itemCount <= 1) {
                                            val text =
                                                if (searchType.equals(BaseActivity.TYPE_FHT))
                                                    "Sorry, we’ve run out of potential flatmates for you."
                                                else if (searchType.equals(BaseActivity.TYPE_DATE))
                                                    "Sorry, we’ve run out of potential members."
                                                else "Sorry, we’ve run out of potential flatmates."
                                            activity.binder.showEmptyData(text)
                                        } else {
                                            activity.userData.removeAt(position)
                                            activity.binder.adapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            })
                    }
                }
            } else if (activity is LikedActivity) {
                if (activity.viewTypeReceivedLikes) {
                    if (details.isLiked)
                        holder.cardLike.visibility = View.GONE
                    else
                        holder.cardLike.visibility = View.VISIBLE
                    holder.cardNotInterested.visibility = View.VISIBLE
                    holder.cardLike.setOnClickListener {
                        // Check if FHT or FMS
                        if (searchType == BaseActivity.TYPE_USER
                        ) {
                            val myFlat =
                                FlatShareApplication.getDbInstance().userDao().getFlatData()
                            if (myFlat == null || myFlat.completed < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                                DialogIncompleteProfile(activity, searchType)
                                return@setOnClickListener
                            }
                        } else {
                            if (AppConstants.loggedInUser?.completed!! < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                                DialogIncompleteProfile(activity, searchType)
                                return@setOnClickListener
                            }
                        }

                        activity.viewBinding.rvLiked.smoothScrollToPosition(position)
                        activity.viewBinding.rvLiked.addOnItemTouchListener(activity.rvScrollStopListener)
                        val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
                            searchType, connectionType, item.id
                        )
                        WebserviceManager().addLike(activity,
                            likeUrl,
                            LikeRequest(data.details.preferredLocation),
                            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                                override fun onCallBackPayment(count: Int) {
                                    activity.viewBinding.rvLiked.removeOnItemTouchListener(activity.rvScrollStopListener)
                                    // Checking payment gateway
                                    PaymentHandler.showPaymentForChecks(activity, null)
                                }

                                override fun onResponseCallBack(response: String) {
                                    activity.viewBinding.rvLiked.removeOnItemTouchListener(activity.rvScrollStopListener)
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
                                        MixpanelUtils.onLiked(item.id, mixpanelSearchType)
                                        if (resp.matched) {
                                            MixpanelUtils.onMatched(item.id, mixpanelSearchType)
                                            showConnectionMatch(
                                                searchType,
                                                activity,
                                                item,
                                                flat?.mongoId
                                            )
                                            if (resp.shouldShowReview())
                                                InAppReview.show(activity)
                                        }
                                        if (activity.apiController.adapter!!.itemCount <= 1) {
                                            activity.apiController.hideView()
                                        } else {
                                            activity.users.removeAt(position)
                                            activity.apiController.adapter?.notifyDataSetChanged()
                                        }
                                    }

                                }
                            })
                    }
                } else {
                    if (AppConstants.isAppLive) {
                        holder.cardLike.visibility = View.GONE
                        holder.cardNotInterested.visibility = View.GONE
                    } else {
                        holder.cardLike.visibility = View.VISIBLE
                        holder.cardNotInterested.visibility = View.GONE
                        holder.cardLike.setOnClickListener {
                            activity.apiManager.exploreDisLike(
                                false,
                                searchType, connectionType,
                                item.id
                            ) { response ->
                                val resp = response as com.joinflatshare.pojo.BaseResponse
                                if (resp.status == 200) {
                                    activity.users.removeAt(position)
                                    activity.apiController.adapter?.notifyDataSetChanged()
                                    if (activity.users.size == 0) {
                                        activity.apiController.hideView()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            holder.feedHolder.setOnClickListener {
                onHolderClick(activity, position, searchType, data)
            }

            holder.cardNotInterested.setOnClickListener {
                notInterested(activity, item.id, position)
            }

            holder.imgOption.setOnClickListener {
                val list = ArrayList<ModelBottomSheet>()
                if (UserShareMessageGenerator.isUserDataAvailableToShare(item)
                    && (searchType == BaseActivity.TYPE_USER || searchType == BaseActivity.TYPE_FHT)
                )
                    list.add(ModelBottomSheet("Copy Link", 2))
                list.add(ModelBottomSheet("Report", 3))
                list.add(ModelBottomSheet("Not Interested", 2))
                BottomSheetView(activity, list).show { view, position ->
                    when (list[position].name) {
                        "Copy Link" -> {
                            activity.apiManager.showProgress()
                            if (searchType == BaseActivity.TYPE_USER) {
                                DeepLinkHandler.createUserSFSDynamicLink(
                                    item
                                ) { text ->
                                    DialogCustomProgress.hideProgress(activity);
                                    if (!text.isNullOrBlank()) {
                                        if (text != "0") {
                                            val shareMessage =
                                                UserShareMessageGenerator.generateUserMessage(item) + "\n\n" + text
                                            CommonMethods.copyToClipboard(activity, shareMessage)
                                        }
                                    }
                                }
                            } else if (searchType == BaseActivity.TYPE_FHT) {
                                DeepLinkHandler.createUserFHTDynamicLink(
                                    item
                                ) { text ->
                                    DialogCustomProgress.hideProgress(activity);
                                    if (!text.isNullOrBlank()) {
                                        if (text != "0") {
                                            val shareMessage =
                                                UserShareMessageGenerator.generateFHTMessage(item) + "\n\n" + text
                                            CommonMethods.copyToClipboard(activity, shareMessage)
                                        }
                                    }
                                }
                            }
                        }

                        "Report" -> {
                            DialogReport(
                                activity, BaseActivity.TYPE_USER, item.id, null
                            ) { text ->
                                if (text.equals("1")) {
                                    if (activity is ExploreActivity) {
                                        activity.userData.removeAt(position)
                                        activity.binder.adapter.notifyDataSetChanged()
                                    } else if (activity is LikedActivity) {
                                        activity.users.removeAt(position)
                                        activity.apiController.adapter?.notifyDataSetChanged()
                                    }
                                }
                            }
                        }

                        "Not Interested" -> {
                            notInterested(activity, item.id, position)
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
                activity.apiManager.revealLikes(
                    connectionType,
                    item.id
                ) { response ->
                    if (response is Int) {
                        PaymentHandler.showPaymentForReveals(
                            activity
                        ) { text ->
                            if (text.equals("1")) {
                                if (activity is LikedActivity) {
                                    activity.apiManager.revealLikes(
                                        connectionType,
                                        item.id
                                    ) {
                                        DialogLottieViewer.loadAnimation(
                                            holder.lottieLike,
                                            R.raw.lottie_reveal,
                                            null
                                        )
                                        activity.users[position].details.revealed = true
                                        activity.apiController.adapter?.notifyItemChanged(
                                            position
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        if (activity is LikedActivity) {
                            DialogLottieViewer.loadAnimation(
                                holder.lottieLike,
                                R.raw.lottie_reveal,
                                null
                            )
                            activity.users[position].details.revealed = true
                            activity.apiController.adapter?.notifyItemChanged(
                                position
                            )
                        }
                    }
                }
            }
        }*/


    private fun notInterested(activity: BaseActivity, id: String, position: Int) {
        /*DialogLottieViewer.loadAnimation(
            holder.lottieLike,
            R.raw.lottie_not_interested,
            null
        )
        activity.apiManager.report(
            false,
            BaseActivity.TYPE_USER, 0, id
        ) { response ->
            val resp = response as com.joinflatshare.pojo.BaseResponse
            if (resp.status == 200) {
                if (activity is ExploreActivity) {
                    activity.userData.removeAt(position)
                    activity.binder.adapter.notifyDataSetChanged()
                } else if (activity is LikedActivity) {
                    activity.users.removeAt(position)
                    activity.apiController.adapter?.notifyDataSetChanged()
                }
            }
        }*/
    }

    private fun onHolderClick(
        activity: BaseActivity,
        position: Int,
        searchType: String,
        data: com.joinflatshare.pojo.explore.UserRecommendationItem
    ) {
        val item = data.data
        val intent = Intent(activity, ProfileDetailsActivity::class.java)
        intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, RouteConstants.ROUTE_CONSTANT_FEED)
        intent.putExtra("phone", item.id)
        intent.putExtra("searchType", searchType)
        CommonMethod.switchActivity(
            activity, intent
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
        data: com.joinflatshare.pojo.explore.UserRecommendationItem
    ) {
        val like = intent?.getBooleanExtra("like", false)
        val chat = intent?.getBooleanExtra("chat", false)
        val report = intent?.getBooleanExtra("report", false)
        /*if (like == true || report == true) {
            if (activity is ExploreActivity) {
                activity.userData.removeAt(position)
                activity.binder.adapter.notifyDataSetChanged()
            }
        } else if (chat == true) {
            data.details.chatRequestSent = true
            if (activity is ExploreActivity) {
                activity.userData[position] = data
                activity.binder.adapter.notifyDataSetChanged()
            } else if (activity is LikedActivity) {
                activity.users[position] = data
                activity.apiController.adapter?.notifyDataSetChanged()
            }
        }*/
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
        } else if (searchType == BaseActivity.TYPE_DATE
        ) {
            DialogConnection(
                activity,
                AppConstants.loggedInUser, null, user,
                "" + AppConstants.loggedInUser?.dateProperties?.dateType
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

    fun isRestricted(response: Any?): Boolean {
        return response is Int
    }
}
package com.joinflatshare.ui.liked

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.databinding.ActivityLikedBinding
import com.joinflatshare.customviews.PaginationScrollListener
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.explore.RecommendationResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class LikedApiController(
    private val activity: LikedActivity,
    private val viewBind: ActivityLikedBinding
) {
    var adapter: ExploreAdapter? = null
    var isDataFetching = false
    var hasMoreData = true
    var currentReceivedPage = 0
    var currentSentPage = 0
    var hasGodModeExpired = false
    var isApiCalledForCountOnly = false

    init {
        val layoutManager = LinearLayoutManager(activity)
        viewBind.rvLiked.layoutManager = layoutManager
        viewBind.rvLiked.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                getLikedList()
            }

            override fun isLastPage(): Boolean {
                return !hasMoreData
            }

            override fun isLoading(): Boolean {
                return isDataFetching
            }
        })
    }

    private fun initAdapter() {
        if (activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_CASUAL
            || activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_LONG_TERM
            || activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS
        )
            adapter =
                ExploreAdapter(activity,  activity.flat)
        else
            adapter =
                ExploreAdapter(activity,  activity.flat)
        viewBind.rvLiked.adapter = adapter
    }

    private val callback = object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
        var searchCount = "0"
        var totalCount = 0
        var lastIndex = -1
        override fun onResponseCallBack(response: String) {
            isDataFetching = false
            if ((activity.viewTypeReceivedLikes && currentReceivedPage == 0)
                || (!activity.viewTypeReceivedLikes && currentSentPage == 0)
            ) {
                activity.flats.clear()
                activity.users.clear()
                adapter = null
            }
            val resp = Gson().fromJson(response, RecommendationResponse::class.java)
            totalCount = resp.count

            if (!isApiCalledForCountOnly) {
                lastIndex = resp.lastIndex

                if (activity.SEARCH_TYPE == BaseActivity.TYPE_FLAT) {
                    val data = resp.flatData
                    activity.flats.addAll(data)
                } else if (activity.SEARCH_TYPE == BaseActivity.TYPE_USER
                    || activity.SEARCH_TYPE == BaseActivity.TYPE_FHT
                    || activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_CASUAL
                    || activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_LONG_TERM
                    || activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS
                ) {
                    val data = resp.userData
                    activity.users.addAll(data)
                }

                if (resp.showingUsers.contains("/"))
                    searchCount =
                        resp.showingUsers.substring(resp.showingUsers.lastIndexOf("/") + 1)


                // check for pagination
                if (activity.viewTypeReceivedLikes) {
                    hasMoreData = lastIndex > currentReceivedPage
                    currentReceivedPage++
                } else {
                    hasMoreData = lastIndex > currentSentPage
                    currentSentPage++
                }
            }
            // check if this is the first call and all total counts have been set
            if (activity.viewTypeReceivedLikes) {
                if (viewBind.txtLikedReceivedCount.text.isEmpty() && viewBind.txtLikedSentCount.text.isEmpty()) {
                    setTotalCount(totalCount, true)
                    setTotalCountInSearch(searchCount)
                    if (searchCount.equals("0")) {
                        if (hasGodModeExpired) {
                            isApiCalledForCountOnly = true
                            getLikedList()
                        } else
                            viewBind.llLikedSent.performClick()
                    } else {
                        isApiCalledForCountOnly = true
                        getLikedList()
                    }
                    return
                } else if (viewBind.txtLikedReceivedCount.text.isNotEmpty() && viewBind.txtLikedSentCount.text.isEmpty()) {
                    isApiCalledForCountOnly = false
                    setTotalCount(totalCount, false)
                } else {
                    setTotalCount(totalCount, true)
                    setTotalCountInSearch(searchCount)
                }
            } else {
                setTotalCount(totalCount, false)
                setTotalCountInSearch(searchCount)
            }
            toggleView()
        }

        override fun onCallBackPayment(count: Int) {
            hasGodModeExpired = true
            totalCount = count
            PaymentHandler.showPaymentForGodMode(
                activity
            ) { text ->
                if (text.equals("1")) {
                    hasGodModeExpired = false
                    activity.viewTypeReceivedLikes = false
                    activity.viewBinding.llLikedReceived.performClick()
                }
            }
        }
    }

    private fun toggleView() {
        if (viewBind.txtLikedReceivedCount.text.equals("0")
            && viewBind.txtLikedSentCount.text.equals("0")
        ) {
            viewBind.llLikedCountHolder.visibility = View.GONE
            viewBind.cardDropdown.visibility = View.GONE
            viewBind.viewLikedLine.visibility = View.GONE
            viewBind.llLikeHolder.visibility = View.GONE
            viewBind.llEmptyLike.visibility = View.VISIBLE
        } else {
            if (adapter == null)
                initAdapter()
            else adapter?.notifyDataSetChanged()
            viewBind.llLikedCountHolder.visibility = View.VISIBLE
            viewBind.cardDropdown.visibility = View.VISIBLE
            if (adapter?.itemCount == 0) {
                hideView()
            } else {
                viewBind.llLikeHolder.visibility = View.VISIBLE
                viewBind.llEmptyLike.visibility = View.GONE
                viewBind.viewLikedLine.visibility = View.VISIBLE
            }
        }
    }

    fun hideView() {
        viewBind.llLikeHolder.visibility = View.GONE
        viewBind.llEmptyLike.visibility = View.VISIBLE
        viewBind.viewLikedLine.visibility = View.GONE
    }

    private fun setTotalCount(totalCount: Int, isViewTypeReceivedLikes: Boolean) {
        if (isViewTypeReceivedLikes)
            viewBind.txtLikedReceivedCount.text = "" + totalCount
        else
            viewBind.txtLikedSentCount.text = "" + totalCount
    }

    private fun setTotalCountInSearch(totalCount: String) {
        var text = ""
        if (activity.SEARCH_TYPE == BaseActivity.TYPE_USER)
            text = "Flatmate Search"
        else if (activity.SEARCH_TYPE == BaseActivity.TYPE_FHT)
            text = "Flathunt Together"
        else if (activity.SEARCH_TYPE == BaseActivity.TYPE_FLAT)
            text = "Shared Flat Search"
        else if (activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_CASUAL)
            text = "Casual Date"
        else if (activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_LONG_TERM)
            text = "Long-Term Partner"
        else if (activity.SEARCH_TYPE == BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS)
            text = "Activity Partners"
        viewBind.txtDropdown.text = text
        viewBind.txtLikedCount.text = "" + totalCount
    }

    fun callApi() {
        isDataFetching = true
        if (activity.viewTypeReceivedLikes) {
            // call received liked api
            getReceivedLikedList()
        } else {
            // call get Likes api
            getLikedList()
        }
    }

    private fun getLikedList() {
        WebserviceManager().getLikesSent(
            activity,
            !isApiCalledForCountOnly,
            activity.SEARCH_TYPE,
            currentSentPage.toString(), callback
        )
    }

    private fun getReceivedLikedList() {
        WebserviceManager().getLikesReceived(
            activity,
            activity.SEARCH_TYPE,
            currentReceivedPage.toString(),
            callback
        )
    }
}
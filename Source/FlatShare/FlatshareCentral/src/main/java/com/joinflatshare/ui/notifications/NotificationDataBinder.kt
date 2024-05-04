package com.joinflatshare.ui.notifications

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityNotificationsBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.customviews.PaginationScrollListener
import com.joinflatshare.pojo.notification.NotificationResponse
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationDataBinder(
    private val activity: NotificationActivity, private val viewBind: ActivityNotificationsBinding

) {
    var isDataFetching = false
    var hasMoreData = true
    var currentPage = 0
    var hasNoDataFromNotification = false
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(activity)
    private val adapter: NotificationAdapter

    init {
        viewBind.rvNotifications.layoutManager = layoutManager
        adapter = NotificationAdapter(activity, activity.notifications)
        viewBind.rvNotifications.adapter = adapter
        setScrollListener()
    }

    fun getNotifications() {
        WebserviceManager().getNotifications(activity, "" + currentPage,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: NotificationResponse? =
                        Gson().fromJson(response, NotificationResponse::class.java)
                    if (resp?.data.isNullOrEmpty()) {
                        hasNoDataFromNotification = true
                        bindFriendRequests()
                    } else {
                        if (currentPage == 0) {
                            activity.notifications.clear()
                            hasNoDataFromNotification = false
                            viewBind.rlNoNotifications.visibility = View.GONE
                            viewBind.llActiveNotifications.visibility = View.VISIBLE
                            bindFriendRequests()
                        }
                        activity.notifications.addAll(resp?.data!!)
                        adapter.notifyDataSetChanged()
                    }
                    hasMoreData = (currentPage < resp?.lastIndex!!)
                    currentPage++
                }
            })
    }

    fun bindFriendRequests() {
        val dao = FlatShareApplication.getDbInstance().requestDao()
        val count = dao.getCount(ChatRequestConstants.FRIEND_REQUEST_CONSTANT)
        if (count == 0) {
            viewBind.txtFriendNames.visibility = View.GONE
            viewBind.frameCountFriendRequest.visibility = View.GONE
        } else {
            viewBind.frameCountFriendRequest.visibility = View.VISIBLE
            viewBind.txtCountFriendRequests.text = "" + count
            val friendRequests = dao.getFriendRequests()
            var show = friendRequests[0].requester?.name?.firstName
            if (count > 1) {
                show += " and " + (friendRequests.size - 1) + " others"
            }
            viewBind.txtFriendNames.visibility = View.VISIBLE
            viewBind.txtFriendNames.text = show
        }
        bindFlatInvitations()
    }

    private fun bindFlatInvitations() {
        val dao = FlatShareApplication.getDbInstance().requestDao()
        val count = dao.getCount(ChatRequestConstants.FLAT_REQUEST_CONSTANT)
        if (count == 0) {
            viewBind.txtInvitationNames.visibility = View.GONE
            viewBind.frameCountInvitationRequest.visibility = View.GONE
        } else {
            viewBind.frameCountInvitationRequest.visibility = View.VISIBLE
            viewBind.txtCountInvitationRequests.text = "" + count
            val flatRequests = dao.getFlatRequests()
            var show = flatRequests[0].requester?.name?.firstName
            show += if (count > 1) {
                " and " + (flatRequests.size - 1) + " others invited you"
            } else " invited you"
            viewBind.txtInvitationNames.visibility = View.VISIBLE
            viewBind.txtInvitationNames.text = show
        }
        getChatRequests()
    }

    private fun getChatRequests() {
        val dao = FlatShareApplication.getDbInstance().requestDao()
        val totalCount = dao.getTotalChatCount()
        if (totalCount == 0) {
            if (hasNoDataFromNotification) {
                viewBind.rlNoNotifications.visibility = View.VISIBLE
                viewBind.llActiveNotifications.visibility = View.GONE
            }
            viewBind.txtChatNames.visibility = View.GONE
            viewBind.frameCountChatRequest.visibility = View.GONE
        } else {
            if (hasNoDataFromNotification) {
                viewBind.rlNoNotifications.visibility = View.GONE
                viewBind.llActiveNotifications.visibility = View.VISIBLE
            }
            viewBind.frameCountChatRequest.visibility = View.VISIBLE
            viewBind.txtCountChatRequest.text = "" + totalCount
            val requestType = dao.getRequestTypeWithMaxChatRequests()
            if (requestType.isBlank())
                viewBind.txtChatNames.visibility = View.GONE
            else {
                val chatRequests = dao.getChatRequests(requestType)
                if (chatRequests.isEmpty()) {
                    viewBind.txtChatNames.visibility = View.GONE
                } else {
                    var show = ""
                    show = if (requestType == ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT ||
                        requestType == ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                        || requestType == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                        || requestType == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                        || requestType == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                    ) {
                        chatRequests[0].requester?.name?.firstName!!
                    } else chatRequests[0].flat?.name!!
                    if (chatRequests.size > 1) {
                        show += " and " + (chatRequests.size - 1) + " others want to connect"
                    } else show += " wants to connect"
                    viewBind.txtChatNames.visibility = View.VISIBLE
                    viewBind.txtChatNames.text = show
                }
            }
        }
    }

    private fun setScrollListener() {
        viewBind.rvNotifications.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                getNotifications()
            }

            override fun isLastPage(): Boolean {
                return !hasMoreData
            }

            override fun isLoading(): Boolean {
                return isDataFetching
            }
        })
    }
}
package com.joinflatshare.ui.notifications.request_friend

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemNotificationsBinding
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.pojo.requests.ConnectionItem
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils.getPostTime
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class FriendRequestAdapter(
    private val activity: FriendRequestActivity,
    private val items: ArrayList<ConnectionItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val activity: FriendRequestActivity,
        private val view: ItemNotificationsBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: FriendRequestAdapter
        ) {
            val item = adapter.items[position]
            ImageHelper.loadImage(
                activity,
                R.drawable.ic_user,
                view.imgNotificationDp,
                ImageHelper.getProfileImageWithAws(item.requester)
            )
            val text = activity.resources.getString(
                R.string.notification_text_friend_invite,
                item.requester?.name?.firstName + " " + item.requester?.name?.lastName,
                getPostTime(item.createdAt)
            )
            view.txtNotification.text = HtmlCompat.fromHtml(
                text,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            if (item.mutuals.isNotEmpty()) {
                view.txtNotificationDesc.visibility = View.VISIBLE
                var mutualText = ""
                if (item.mutuals.size > 1) {
                    mutualText = activity.resources.getString(
                        R.string.notification_text_invite_connection, "Friends with ",
                        item.mutuals[0].name?.firstName, "& ${item.mutuals.size - 1} others"
                    )
                } else mutualText = activity.resources.getString(
                    R.string.notification_text_invite_connection, "Friends with ",
                    item.mutuals[0].name?.firstName, ""
                )
                view.txtNotificationDesc.text = HtmlCompat.fromHtml(
                    mutualText,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else view.txtNotificationDesc.visibility = View.GONE
            view.llNotificationAccept.setOnClickListener {
                updateRequest(item, position, adapter, true)
            }
            view.llNotificationDecline.setOnClickListener {
                updateRequest(item, position, adapter, false)
            }
            view.imgNotificationDp.setOnClickListener {
                val intent = Intent(activity, ProfileDetailsActivity::class.java)
                intent.putExtra(
                    RouteConstants.ROUTE_CONSTANT_FROM,
                    RouteConstants.ROUTE_CONSTANT_FRIEND_REQUEST
                )
                intent.putExtra("notificationId", item.notiID)
                intent.putExtra("phone", item.requester?.id)
                CommonMethod.switchActivity(
                    activity,
                    intent
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        activity.setResult(Activity.RESULT_OK)
                        if (adapter.items.size == 1) {
                            CommonMethod.finishActivity(activity)
                        } else {
                            adapter.items.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
        }

        private fun updateRequest(
            item: ConnectionItem,
            position: Int,
            adapter: FriendRequestAdapter,
            isAccept: Boolean
        ) {
            if (isAccept) {
                activity.apiManager.acceptFriendRequest(
                    item.requester?.id!!
                ) { response ->
                    val resp = response as com.joinflatshare.pojo.BaseResponse
                    processResult(resp, adapter, position, isAccept)
                }
            } else {
                activity.apiManager.rejectFriendRequest(
                    item.requester?.id!!
                ) { response ->
                    val resp = response as com.joinflatshare.pojo.BaseResponse
                    processResult(resp, adapter, position, isAccept)
                }
            }
        }

        private fun processResult(
            resp: com.joinflatshare.pojo.BaseResponse,
            adapter: FriendRequestAdapter,
            position: Int,
            isAccept: Boolean
        ) {
            if (resp.status == 200) {
                if (isAccept)
                    MixpanelUtils.onFriendRequestResponded(adapter.items[position].requester)
                // TODO Removing the below code for sendbird as it is implemented in backend
                /*if (isAccept) {
                    val user = User()
                    val item = adapter.items[position]
                    user.id = item.requester?.id!!
                    user.name = item.requester.name
                    activity.sendBirdUserChannel.joinChannel(
                        user, SendBirdConstants.CHANNEL_TYPE_FRIEND, null
                    )
                }*/
                // Remove from DB
                FlatShareApplication.getDbInstance().requestDao()
                    .deleteRequest(adapter.items[position].notiID)

                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                activity.setResult(Activity.RESULT_OK)
            }
        }
    }
}
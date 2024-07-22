package com.joinflatshare.ui.checks.request_chat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemNotificationsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.inapp_review.InAppReview
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.requests.ConnectionItem
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ChatRequestAdapter(
    private val activity: ChatRequestActivity,
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
        private val activity: ChatRequestActivity,
        private val view: ItemNotificationsBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: ChatRequestAdapter
        ) {
            val item = adapter.items[position]

            var default = 0
            val link: String?
            if (activity.type.equals(ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U)) {
                default = R.drawable.ic_flat_bg
                link = ImageHelper.getFlatDpWithAws(item.flat?.id)
                view.txtNotification.text = item.flat?.name
            } else {
                default = R.drawable.ic_user
                link = ImageHelper.getProfileImageWithAws(item.requester)
                view.txtNotification.text =
                    "${item.requester?.name?.firstName} ${item.requester?.name?.lastName}"
            }
            ImageHelper.loadImage(
                activity, default, view.imgNotificationDp, link
            )
            view.txtNotificationDate.text = DateUtils.getPostTime(item.createdAt)
            view.txtNotificationDate.visibility = View.VISIBLE
            if (item.mutuals.isEmpty())
                view.txtNotificationDesc.visibility = View.GONE
            else {
                view.txtNotificationDesc.visibility = View.VISIBLE
                var others = ""
                if (item.mutuals.size > 1)
                    others += " & ${item.mutuals.size - 1} others"
                val text = activity.resources.getString(
                    R.string.notification_text_invite_connection,
                    "Friends with",
                    item.mutuals.get(0).name?.firstName,
                    others
                )
                view.txtNotificationDesc.text = HtmlCompat.fromHtml(
                    text,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            view.llNotificationAccept.setOnClickListener {
                updateRequest(item, bindingAdapterPosition, adapter, true)
            }
            view.llNotificationDecline.setOnClickListener {
                updateRequest(item, bindingAdapterPosition, adapter, false)
            }
            view.imgNotificationDp.setOnClickListener {
                val intent: Intent
                if (activity.type == ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U) {
                    intent = Intent(activity, FlatDetailsActivity::class.java)
                    intent.putExtra("phone", item.flat?.id)
                } else {
                    intent = Intent(activity, ProfileDetailsActivity::class.java)
                    intent.putExtra("phone", item.requester?.id)
                }
                intent.putExtra("notificationId", item.notiID)
                intent.putExtra("connectionType", activity.type)
                intent.putExtra(
                    RouteConstants.ROUTE_CONSTANT_FROM,
                    RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST
                )
                CommonMethod.switchActivity(
                    activity,
                    intent
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        activity.setResult(Activity.RESULT_OK)
                        if (activity.chatRequests.size == 1) {
                            CommonMethod.finishActivity(activity)
                        } else {
                            activity.chatRequests.removeAt(position)
                            activity.setTextCount()
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
        }

        private fun updateRequest(
            item: ConnectionItem,
            position: Int,
            adapter: ChatRequestAdapter,
            isAccept: Boolean
        ) {
            WebserviceManager().sendChatRequestResponse(
                activity,
                isAccept,
                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
                item.requester?.id!!,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        val resp = Gson().fromJson(response, BaseResponse::class.java)
                        processResult(resp, adapter, position, isAccept)
                    }
                })
        }

        private fun processResult(
            resp: BaseResponse,
            adapter: ChatRequestAdapter,
            position: Int,
            isAccept: Boolean
        ) {
            if (resp.status == 200) {
                if (isAccept) {
                    val item = adapter.items[position]
                    var otherId = ""

                    if (activity.type == ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F) {
                        val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                        if (flat != null && item.requester != null) {
                            val tempUser = com.joinflatshare.pojo.user.User()
                            tempUser.id = item.requester?.id!!
                            tempUser.dp = item.requester?.dp
                            tempUser.name = item.requester?.name
                            DialogConnection(
                                activity,
                                tempUser,
                                flat.mongoId, null,
                                activity.type
                            )
                            MixpanelUtils.onChatRequestAccepted(item.requester?.id!!, flat.id)
                        }
                    } else if (activity.type == ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U) {
                        DialogConnection(
                            activity,
                            AppConstants.loggedInUser,
                            item.flat!!.id,
                            null,
                            activity.type
                        )
                        MixpanelUtils.onChatRequestAccepted(
                            item.flat?.id!!, AppConstants.loggedInUser?.id!!
                        )
                    } else if (activity.type == ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        || activity.type == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                        || activity.type == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                        || activity.type == "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                    ) {
                        val tempUser = com.joinflatshare.pojo.user.User()
                        tempUser.id = item.requester?.id!!
                        tempUser.dp = item.requester?.dp
                        tempUser.name = item.requester?.name
                        DialogConnection(
                            activity,
                            AppConstants.loggedInUser,
                            null,
                            tempUser,
                            activity.type
                        )
                        MixpanelUtils.onChatRequestAccepted(
                            item.requester?.id!!, AppConstants.loggedInUser?.id!!
                        )
                    }
                    InAppReview.show(activity)
                }
                // Remove from DB
                FlatShareApplication.getDbInstance().requestDao()
                    .deleteRequest(adapter.items[position].notiID)
                activity.chatRequests.removeAt(position)
                activity.setTextCount()
                adapter.notifyItemRemoved(position)
                activity.setResult(Activity.RESULT_OK)
            }
        }
    }
}
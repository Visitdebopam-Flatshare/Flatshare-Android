package com.joinflatshare.ui.checks.request_invitation

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
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.pojo.requests.FlatInviteItem
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.Prefs

class InvitationRequestAdapter(
    private val activity: InvitationRequestActivity,
    private val items: ArrayList<FlatInviteItem>
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
        private val activity: InvitationRequestActivity,
        private val view: ItemNotificationsBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: InvitationRequestAdapter
        ) {
            val item = adapter.items[position]
            ImageHelper.loadImage(
                activity, R.drawable.ic_user, view.imgNotificationDp,
                ImageHelper.getProfileImageWithAws(item.requester)
            )
            val text = activity.resources.getString(
                R.string.notification_text_invite_flat,
                item.requester?.name?.firstName + " " + item.requester?.name?.lastName,
                " invited you to join the flat ",
                item.flat?.data?.name
            )
            view.txtNotification.text = HtmlCompat.fromHtml(
                text,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            view.txtNotificationDate.text = DateUtils.getPostTime(item.createdAt)
            view.txtNotificationDesc.visibility = View.GONE
            view.llNotificationAccept.setOnClickListener {
                updateRequest(item, position, adapter, true)
            }
            view.llNotificationDecline.setOnClickListener {
                updateRequest(item, position, adapter, false)
            }
            view.imgNotificationDp.setOnClickListener {
                val intent = Intent(activity, FlatDetailsActivity::class.java)
                intent.putExtra(
                    RouteConstants.ROUTE_CONSTANT_FROM,
                    RouteConstants.ROUTE_CONSTANT_FLAT_REQUEST
                )
                intent.putExtra("phone", item.flat?.data?.id)
                intent.putExtra("notificationId", item.notiID)
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
            item: FlatInviteItem,
            position: Int,
            adapter: InvitationRequestAdapter,
            isAccept: Boolean
        ) {
            val flatMondoId = FlatShareApplication.getDbInstance().userDao().getFlatData()?.mongoId
            if (isAccept) {
                activity.apiManager.acceptFlatInvitation(
                    item.requester?.id!!
                ) { response ->
                    val resp = response as com.joinflatshare.pojo.BaseResponse
                    CommonMethod.clearFlat()
                    AppConstants.isFeedReloadRequired = true
                    activity.prefs.setString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "1")
                    processResult(resp, adapter, position, isAccept, flatMondoId)
                }
            } else {
                activity.apiManager.rejectFlatInvitation(
                    item.requester?.id!!
                ) { response ->
                    val resp = response as com.joinflatshare.pojo.BaseResponse
                    processResult(resp, adapter, position, isAccept, flatMondoId)
                }
            }
        }

        private fun processResult(
            resp: com.joinflatshare.pojo.BaseResponse,
            adapter: InvitationRequestAdapter,
            position: Int,
            isAccept: Boolean,
            oldFlatMongoId: String?
        ) {
            if (resp.status == 200) {
                if (isAccept)
                    MixpanelUtils.onFlatmateInvitationResponded(
                        adapter.items[position].requester
                    )
                // TODO Removing the below code for sendbird as it is implemented in backend
                /*if (isAccept) {
                    val item = adapter.items[position]
                    if (oldFlatMongoId != null) {
                        // Leave my flat group
                        activity.channel.removeFlatMember(
                            AppConstants.loggedInUser?.id!!,
                            oldFlatMongoId
                        )
                        // Leave all flatmate groups
                        activity.channelFlatMate.removeFlatMember(
                            oldFlatMongoId, AppConstants.loggedInUser?.id!!
                        )
                        activity.channelFlatMate.removeUserFromAllFlatmates(
                            oldFlatMongoId,
                            AppConstants.loggedInUser?.id!!
                        )
                    }*/
                // Join new channel
                /*activity.channel.addFlatMember(
                    item.flat?.data?.mongoId!!,
                    AppConstants.loggedInUser?.id!!,
                    SendBirdConstants.CHANNEL_TYPE_FLAT,
                    null
                )
            }*/
                // Remove from DB
                FlatShareApplication.getDbInstance().requestDao()
                    .deleteRequest(adapter.items[position].notiID!!)
                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                activity.setResult(Activity.RESULT_OK)
            }
        }
    }
}
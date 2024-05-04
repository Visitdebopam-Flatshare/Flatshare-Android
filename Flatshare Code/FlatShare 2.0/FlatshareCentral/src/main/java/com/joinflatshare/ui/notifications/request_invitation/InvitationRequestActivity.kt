package com.joinflatshare.ui.notifications.request_invitation

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRequestInvitationBinding
import com.joinflatshare.chat.SendBirdFlatChannel
import com.joinflatshare.chat.SendBirdFlatmateChannel
import com.joinflatshare.pojo.requests.FlatInviteItem
import com.joinflatshare.ui.base.BaseActivity

class InvitationRequestActivity : BaseActivity() {
    private lateinit var viewBind: ActivityRequestInvitationBinding
    var type = ""
    val flatRequests = ArrayList<FlatInviteItem>()
    private var adapter: InvitationRequestAdapter? = null
    lateinit var channel: SendBirdFlatChannel
    lateinit var channelFlatMate: SendBirdFlatmateChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRequestInvitationBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Invitations", 0, 0)
        init()
        getInvitations()
    }

    private fun init() {
        channel = SendBirdFlatChannel(this)
        channelFlatMate = SendBirdFlatmateChannel(this)
        viewBind.rvInvitationRequest.layoutManager = LinearLayoutManager(this)
        adapter = InvitationRequestAdapter(this, flatRequests)
        viewBind.rvInvitationRequest.adapter = adapter
    }

    private fun getInvitations() {
        flatRequests.addAll(FlatShareApplication.getDbInstance().requestDao().getFlatRequests())
        adapter?.notifyDataSetChanged()
    }
}
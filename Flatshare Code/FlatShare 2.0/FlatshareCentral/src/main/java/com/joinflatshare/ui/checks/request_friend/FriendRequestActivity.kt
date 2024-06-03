package com.joinflatshare.ui.checks.request_friend

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRequestInvitationBinding
import com.joinflatshare.chat.SendBirdUserChannel
import com.joinflatshare.pojo.requests.ConnectionItem
import com.joinflatshare.ui.base.BaseActivity

class FriendRequestActivity : BaseActivity() {
    private lateinit var viewBind: ActivityRequestInvitationBinding
    var type = ""
    val friendRequests = ArrayList<ConnectionItem>()
    private var adapter: FriendRequestAdapter? = null
    lateinit var sendBirdUserChannel: SendBirdUserChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRequestInvitationBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Friend Requests", 0, 0)
        init()
        getRequests()
    }

    private fun init() {
        sendBirdUserChannel = SendBirdUserChannel(this)
        viewBind.rvInvitationRequest.layoutManager = LinearLayoutManager(this)
        adapter = FriendRequestAdapter(this, friendRequests)
        viewBind.rvInvitationRequest.adapter = adapter
    }

    private fun getRequests() {
        friendRequests.addAll(FlatShareApplication.getDbInstance().requestDao().getFriendRequests())
        adapter?.notifyDataSetChanged()
    }
}
package com.joinflatshare.ui.chat.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatListBinding
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.chat.ChatBaseActivity
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.sendbird.android.channel.GroupChannel

class ChatListActivity : ChatBaseActivity() {
    var allGroupChannelList = ArrayList<GroupChannel>()
    var searchedGroupChannelList = ArrayList<GroupChannel>()
    private lateinit var viewBind: ActivityChatListBinding
    var adapter: ChatListAdapter? = null
    val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_CHAT_LIST"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        init()
        ChatListListener(this, viewBind)
        MixpanelUtils.onScreenOpened("Chat List")
        handleNotification()
    }

    override fun onResume() {
        super.onResume()
        AppConstants.menuSelected = 2
        baseViewBinder.applyMenuClick()
        initialiseChat(object : OnStringFetched {
            override fun onFetched(text: String?) {
                getChatList()
                ChatListHandler(this@ChatListActivity)
            }

        })
    }


    private fun init() {
        viewBind.rvFriends.layoutManager = LinearLayoutManager(this)
        adapter = ChatListAdapter(this, searchedGroupChannelList)
        viewBind.rvFriends.adapter = adapter
    }

    private fun getChatList() {
        allGroupChannelList.clear()
        searchedGroupChannelList.clear()
        sendBirdChannel.getChannelList(object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: List<GroupChannel>) {
                if (allChannels.isEmpty()) {
                    viewBind.llChatHolder.visibility = View.GONE
                    viewBind.llChatEmpty.visibility = View.VISIBLE
                } else {
                    viewBind.llChatHolder.visibility = View.VISIBLE
                    viewBind.llChatEmpty.visibility = View.GONE
                    allGroupChannelList.addAll(allChannels)
                    searchedGroupChannelList.addAll(allChannels)
                    if (adapter != null) adapter!!.notifyDataSetChanged()
                }
            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>) {}
        })
    }

    private fun handleNotification() {
        if (intent.hasExtra("channel")) {
            intent.setClass(this, ChatDetailsActivity::class.java)
            CommonMethod.switchActivity(this, intent, false)
        }
    }
}
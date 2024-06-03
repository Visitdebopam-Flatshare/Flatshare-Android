package com.joinflatshare.ui.checks.request_chat

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRequestChatBinding
import com.joinflatshare.chat.SendBirdFlatmateChannel
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.requests.ConnectionItem
import com.joinflatshare.ui.base.BaseActivity

class ChatRequestActivity : BaseActivity() {
    private lateinit var viewBind: ActivityRequestChatBinding
    var type=""
    val chatRequests = ArrayList<ConnectionItem>()
    private var adapter: ChatRequestAdapter? = null
    lateinit var channel: SendBirdFlatmateChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRequestChatBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Chat Requests", 0, 0)
        init()
    }

    private fun init() {
        channel = SendBirdFlatmateChannel(this)
        viewBind.rvChatRequest.layoutManager = LinearLayoutManager(this)
        adapter = ChatRequestAdapter(this@ChatRequestActivity, chatRequests)
        viewBind.rvChatRequest.adapter = adapter
        ChatRequestListener(this, viewBind)
        findRequestType()
        getChatRequest()
    }

    private fun findRequestType() {
        val dao = FlatShareApplication.getDbInstance().requestDao()
//        type = dao.getRequestTypeWithMaxChatRequests()
//        if (type.isNullOrBlank()) {
            type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
//        }
    }

    fun getChatRequest() {
        chatRequests.clear()
        chatRequests.addAll(
            FlatShareApplication.getDbInstance().requestDao().getChatRequests(type!!)
        )
        if (chatRequests.size == 0)
            hideView()
        else {
            showView()
            adapter?.notifyDataSetChanged()
        }
        setTextCount()
    }

    fun setTextCount() {
        val dao = FlatShareApplication.getDbInstance().requestDao()
        val totalCount = dao.getTotalChatCount()
        var message = ""
        when (type) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F -> {
                val countU2F = dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F)
                message = "Flatmate Search ($countU2F)"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U -> {
                val countF2U = dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U)
                message = "Shared Flat Search ($countF2U)"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT -> {
                val countFHT = dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT)
                message = "Flathunt Together ($countFHT)"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL.toString() -> {
                val casualCount =
                    dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL.toString())
                message = "Casual Date ($casualCount)"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM.toString() -> {
                val longTermCount =
                    dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM.toString())
                message = "Long-Term Partner ($longTermCount)"
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS.toString() -> {
                val partnerCount =
                    dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS.toString())
                message = "Activity Partners ($partnerCount)"
            }

        }
        ((viewBind.llChatReceived.getChildAt(1) as LinearLayout).getChildAt(0) as TextView).text =
            "" + totalCount
        viewBind.txtChat.text = message
    }

    private fun hideView() {
        /*if (AppConstants.loggedInUser?.isFHTSearch?.value == true
            && AppConstants.loggedInUser?.isFlatSearch?.value == true
        ) {
            viewBind.llChatReceived.visibility = View.VISIBLE
            viewBind.viewChatLine.visibility = View.VISIBLE
            viewBind.cardChat.visibility = View.VISIBLE
        } else {
            viewBind.llChatReceived.visibility = View.GONE
            viewBind.viewChatLine.visibility = View.GONE
            viewBind.cardChat.visibility = View.GONE
        }*/
        viewBind.llEmptyRequest.visibility = View.VISIBLE
        viewBind.rvChatRequest.visibility = View.GONE
    }

    private fun showView() {
        viewBind.llEmptyRequest.visibility = View.GONE
        viewBind.rvChatRequest.visibility = View.VISIBLE
        /*viewBind.llChatReceived.visibility = View.VISIBLE
        viewBind.viewChatLine.visibility = View.VISIBLE
        viewBind.cardChat.visibility = View.VISIBLE*/
    }
}
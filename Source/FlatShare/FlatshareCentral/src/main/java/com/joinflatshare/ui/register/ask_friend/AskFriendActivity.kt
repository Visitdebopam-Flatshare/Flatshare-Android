package com.joinflatshare.ui.register.ask_friend

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityAskFriendBinding
import com.joinflatshare.constants.ContactConstants
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.pojo.user.User
import com.joinflatshare.services.MutualContactHandler
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.helper.CommonMethod

class AskFriendActivity : RegisterBaseActivity() {
    private lateinit var viewBind: ActivityAskFriendBinding
    lateinit var apiController: AskFriendApiController
    val contactMap = HashMap<String, Name?>()
    val allAppUsers = ArrayList<User>()
    val allAppUsersId = ArrayList<String>()
    var phone: String? = null
    lateinit var adapter: AskFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityAskFriendBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
    }

    private fun init() {
        phone = intent.getStringExtra("phone")
        viewBind.imgTopbarBack.setOnClickListener {
            CommonMethod.finishActivity(this)
        }
        if (ContactConstants.allContacts.size == 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                MutualContactHandler.getContacts(this)
                showFriends()
            }, 1000)
        } else showFriends()
    }

    private fun showFriends() {
        for (user in ContactConstants.allContacts)
            contactMap.put(user.id, user.name)
        apiController = AskFriendApiController(this)
        apiController.checkStatusFromContacts()
    }

    fun showFriendList() {
        if (allAppUsers.size == 0) {
            viewBind.llAskFriendNoContent.visibility = View.VISIBLE
            viewBind.llAskFriendContent.visibility = View.GONE
        } else {
            viewBind.llAskFriendContent.visibility = View.VISIBLE
            viewBind.llAskFriendNoContent.visibility = View.GONE
            viewBind.rvAskInvite.layoutManager = LinearLayoutManager(this)
            adapter = AskFriendAdapter(this, allAppUsers)
            viewBind.rvAskInvite.adapter = adapter
        }
    }
}
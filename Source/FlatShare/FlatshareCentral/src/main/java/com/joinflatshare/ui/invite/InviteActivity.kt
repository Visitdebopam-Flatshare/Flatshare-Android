package com.joinflatshare.ui.invite

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityInviteBinding
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils.onButtonClicked
import com.joinflatshare.utils.permission.PermissionUtil

class InviteActivity : BaseActivity() {
    lateinit var viewBind: ActivityInviteBinding
    lateinit var adapter: InviteAdapter
    var selectedType = ""
    val displayUsers = ArrayList<User>()
    val nameHashMap = HashMap<String, Name>()
    lateinit var contactsApiController: InviteContactsApiController

    lateinit var apiController: InviteApiController

    companion object {
        const val INVITE_TYPE_APP = "0"
        const val INVITE_TYPE_FRIEND = "1"
        const val INVITE_TYPE_FLAT = "2"
        const val INVITE_TYPE_CONNECTION = "3"
        const val INVITE_STATUS_REGISTERED = "11"
        const val INVITE_STATUS_APP_INVITED = "12"
        const val INVITE_STATUS_UNREGISTERED = "13"
        const val INVITE_STATUS_FRIEND_INVITED = "14"
        const val INVITE_STATUS_FRIENDS = "15"
        const val INVITE_STATUS_FLAT_INVITED = "16"
        const val INVITE_STATUS_FLATMATE = "17"
        const val INVITE_STATUS_SUGGESTED_USERS = "18"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityInviteBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        selectedType = intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM).toString()
        showTopBar(
            this, true, if (selectedType.equals(INVITE_TYPE_APP)) "Invite Friends" else
                if (selectedType == INVITE_TYPE_FLAT) "Add Flatmates" else "Add Friends", 0, 0
        )
        init()
    }

    private fun init() {
        viewBind.rvInvite.layoutManager = LinearLayoutManager(this)
        adapter = InviteAdapter(this, displayUsers)
        viewBind.rvInvite.adapter = adapter
        apiController = InviteApiController(this)
        contactsApiController = InviteContactsApiController(this, viewBind)
        viewBind.llInvite.visibility = View.GONE
        getContactPermission()
        when (selectedType) {
            INVITE_TYPE_APP -> {
                viewBind.llInvite.visibility = View.VISIBLE
                viewBind.txtInviteCount.text =
                    "You have ${
                        FlatShareApplication.getDbInstance().userDao().getUser()?.invites
                    } invites left"
                onButtonClicked("Invite Friends")
            }
            INVITE_TYPE_FRIEND -> {
                onButtonClicked("Add Friends")
            }
            INVITE_TYPE_FLAT -> {
                onButtonClicked("Add Flatmate");
            }
        }
    }

    private fun getContactPermission() {
        PermissionUtil.validatePermission(
            this,
            Manifest.permission.READ_CONTACTS,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (granted) {
                        getAllUsers()
                    } else CommonMethod.finishActivity(this@InviteActivity)
                }
            })
    }

    private fun getAllUsers() {
        InviteListener(this, viewBind)
    }
}
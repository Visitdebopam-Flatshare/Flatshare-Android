package com.joinflatshare.ui.friends

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import com.joinflatshare.FlatshareCentral.databinding.ActivityFriendListBinding
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.utils.helper.CommonMethod

class FriendListListener(
    private val activity: FriendListActivity,
    private val viewBind: ActivityFriendListBinding
) {

    init {
        viewBind.btnAdd.setOnClickListener {
            val intent = Intent(activity, InviteActivity::class.java)
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, InviteActivity.INVITE_TYPE_FRIEND)
            CommonMethod.switchActivity(activity, intent, false)
        }
        viewBind.btnInvite.setOnClickListener {
            val intent = Intent(activity, InviteActivity::class.java)
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, InviteActivity.INVITE_TYPE_FRIEND)
            CommonMethod.switchActivity(activity, intent, false)
        }
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.getFriends()
            viewBind.pullToRefresh.isRefreshing = false
        }
        search()
    }

    private fun search() {
        viewBind.edtInvite.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString().trim()
                activity.shortList.clear()
                if (query.isBlank()) {
                    activity.shortList.addAll(activity.mainList)
                } else {
                    for (temp in activity.mainList) {
                        if (temp.name?.firstName?.lowercase()!!.contains(query.lowercase())
                            || temp.name?.lastName?.lowercase()!!.contains(query.lowercase())
                        ) {
                            activity.shortList.add(temp)
                        }
                    }
                    activity.sortListOnFscore()
                }
                activity.adapter.notifyDataSetChanged()
            }

        })
    }
}
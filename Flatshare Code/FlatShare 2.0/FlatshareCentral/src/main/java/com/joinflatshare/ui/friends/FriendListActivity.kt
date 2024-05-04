package com.joinflatshare.ui.friends

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.databinding.ActivityFriendListBinding
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.pojo.friend_list.FriendListResponse
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response


class FriendListActivity : BaseActivity() {
    private lateinit var viewBind: ActivityFriendListBinding
    val mainList = ArrayList<User>()
    val shortList = ArrayList<User>()
    lateinit var adapter: FriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Friends", 0, 0)
        init()
        FriendListListener(this, viewBind)
        MixpanelUtils.onScreenOpened("Friends List")
    }

    override fun onResume() {
        super.onResume()
        getFriends()
    }

    private fun init() {
        val spanCount = 3
        val spacing = 25
        val includeEdge = false
        viewBind.rvFriends.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        viewBind.rvFriends.layoutManager = GridLayoutManager(this, spanCount)
        adapter = FriendListAdapter(this, shortList)
        viewBind.rvFriends.adapter = adapter
        viewBind.rvFriends.requestFocus()
        viewBind.rvFriends.requestFocusFromTouch()
    }

    fun getFriends() {
        WebserviceManager().getFriendList(this, object :
            OnFlatshareResponseCallBack<Response<ResponseBody>> {
            override fun onResponseCallBack(response: String) {
                val resp: FriendListResponse? =
                    Gson().fromJson(response, FriendListResponse::class.java)
                if (!resp?.data.isNullOrEmpty()) {
                    viewBind.rlFriendsHolder.visibility = View.VISIBLE
                    viewBind.llNofriends.visibility = View.GONE
                    shortList.clear()
                    mainList.clear()
                    mainList.addAll(resp?.data!!)
                    shortList.addAll(mainList)
                    sortListOnFscore()
                    adapter.notifyDataSetChanged()
                    viewBind.txtFriendsCount.text = "${mainList.size}"
                } else {
                    viewBind.rlFriendsHolder.visibility = View.GONE
                    viewBind.llNofriends.visibility = View.VISIBLE
                }
            }
        })
    }

    fun sortListOnFscore() {
        if (shortList.isNotEmpty())
            shortList.sortWith { lhs, rhs -> rhs!!.score.compareTo(lhs!!.score) }
    }
}
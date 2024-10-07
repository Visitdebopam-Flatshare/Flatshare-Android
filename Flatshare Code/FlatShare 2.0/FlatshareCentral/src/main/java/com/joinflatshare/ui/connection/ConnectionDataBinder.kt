package com.joinflatshare.ui.connection

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.databinding.ActivityConnectionListBinding
import com.joinflatshare.customviews.PaginationScrollListener
import com.joinflatshare.pojo.explore.RecommendationResponse
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.pojo.requests.ConnectionListResponse
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ConnectionDataBinder(
    private val activity: ConnectionListActivity,
    private val viewBind: ActivityConnectionListBinding

) {
    var isDataFetching = false
    var hasMoreData = true
    var currentPage = 0
    val list = ArrayList<UserRecommendationItem>()
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(activity)
    val adapter: ConnectionAdapter

    init {
        viewBind.rvConnections.layoutManager = layoutManager
        adapter = ConnectionAdapter(activity, list)
        viewBind.rvConnections.adapter = adapter
//        setScrollListener()
        viewBind.pullToRefresh.setOnRefreshListener {
            activity.fetchData()
            viewBind.pullToRefresh.isRefreshing = false
        }
    }


    internal fun getRequests() {
        isDataFetching = true
        WebserviceManager().getConnectionList(
            activity, responseHandler
        )
    }

    private val responseHandler = object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
        override fun onResponseCallBack(response: String) {
            isDataFetching = false
            val resp = Gson().fromJson(response, ConnectionListResponse::class.java)
            val data = resp.data
            if (data.isNullOrEmpty()) {
                hasMoreData = false
                if (currentPage == 0) {
                    viewBind.pullToRefresh.visibility = View.GONE
                    viewBind.rlNoConnections.visibility = View.VISIBLE
                }
            } else {
                viewBind.pullToRefresh.visibility = View.VISIBLE
                viewBind.rlNoConnections.visibility = View.GONE
                data.forEach {
                    val item = UserRecommendationItem()
                    item.data = it
                    list.add(item)
                }
                adapter.notifyDataSetChanged()
//                hasMoreData = currentPage < resp.lastIndex
//                currentPage++
            }
        }
    }

    private fun setScrollListener() {
        viewBind.rvConnections.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                getRequests()
            }

            override fun isLastPage(): Boolean {
                return !hasMoreData
            }

            override fun isLoading(): Boolean {
                return isDataFetching
            }
        })
    }
}
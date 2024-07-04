package com.joinflatshare.ui.checks

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityCheckListBinding
import com.joinflatshare.customviews.PaginationScrollListener
import com.joinflatshare.pojo.explore.RecommendationResponse
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ChecksDataBinder(
    private val activity: ChecksActivity, private val viewBind: ActivityCheckListBinding

) {
    var isDataFetching = false
    var hasMoreData = true
    var currentPage = 0
    val list = ArrayList<UserRecommendationItem>()
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(activity)
    val adapter: ChecksAdapter

    init {
        viewBind.rvChecks.layoutManager = LinearLayoutManager(activity)
        adapter = ChecksAdapter(activity, list)
        viewBind.rvChecks.adapter = adapter
        prepareButtons()
        setScrollListener()
    }


    internal fun prepareButtons() {
        when (activity.mode) {
            ChecksActivity.MODE_CHECKS -> {
                viewBind.txtChecks.setTextColor(ContextCompat.getColor(activity, R.color.blue_dark))
                viewBind.viewChecks.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                viewBind.txtSuperChecks.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.grey3
                    )
                )
                viewBind.viewSuperChecks.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.color_blue_light
                    )
                )
            }

            ChecksActivity.MODE_SUPER_CHECKS -> {
                viewBind.txtSuperChecks.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                viewBind.viewSuperChecks.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                viewBind.txtChecks.setTextColor(ContextCompat.getColor(activity, R.color.grey3))
                viewBind.viewChecks.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.color_blue_light
                    )
                )
            }
        }
        when (activity.source) {
            ChecksActivity.SOURCE_RECEIVED -> {
                viewBind.viewReceived.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.drawable_button_blue_stroke_blue_bg
                )
                viewBind.txtReceived.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                viewBind.viewSent.background = null
                viewBind.txtSent.setTextColor(ContextCompat.getColor(activity, R.color.grey3))
            }

            ChecksActivity.SOURCE_SENT -> {
                viewBind.viewSent.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.drawable_button_blue_stroke_blue_bg
                )
                viewBind.txtSent.setTextColor(ContextCompat.getColor(activity, R.color.blue_dark))
                viewBind.viewReceived.background = null
                viewBind.txtReceived.setTextColor(ContextCompat.getColor(activity, R.color.grey3))
            }
        }
    }

    internal fun getRequests() {
        isDataFetching = true
        when (activity.mode) {
            ChecksActivity.MODE_CHECKS -> {
                when (activity.source) {
                    ChecksActivity.SOURCE_SENT -> {
                        WebserviceManager().getLikesSent(
                            activity,
                            true,
                            BaseActivity.TYPE_FHT,
                            currentPage, responseHandler
                        )
                    }

                    ChecksActivity.SOURCE_RECEIVED -> {
                        WebserviceManager().getLikesReceived(
                            activity,
                            BaseActivity.TYPE_FHT,
                            currentPage, responseHandler
                        )
                    }
                }
            }

            ChecksActivity.MODE_SUPER_CHECKS -> {
                hasMoreData = false
                RequestHandler.getFHTRequests(activity) {
                    val dao = FlatShareApplication.getDbInstance().requestDao()
                    val data = dao.getChatRequests(BaseActivity.TYPE_FHT)
                    if (data.isEmpty()) {
                        hasMoreData = false
                        viewBind.pullToRefresh.visibility = View.GONE
                        viewBind.rlNoChecks.visibility = View.VISIBLE
                        viewBind.txtNoData.text = "Superchecks you receive will show up here."
                    } else {
                        viewBind.pullToRefresh.visibility = View.VISIBLE
                        viewBind.rlNoChecks.visibility = View.GONE
                        list.clear()
                        data.forEach {
                            if (it.requester != null) {
                                val item = UserRecommendationItem()
                                item.data = it.requester!!
                                list.add(item)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private val responseHandler = object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
        override fun onResponseCallBack(response: String) {
            isDataFetching = false
            val resp = Gson().fromJson(response, RecommendationResponse::class.java)
            val data = resp.userData
            if (data.isEmpty()) {
                hasMoreData = false
                if (currentPage == 0) {
                    viewBind.pullToRefresh.visibility = View.GONE
                    viewBind.rlNoChecks.visibility = View.VISIBLE
                    viewBind.txtNoData.text =
                        if (activity.source == ChecksActivity.SOURCE_SENT) "Checks you send will show up here." else "Checks you receive will show up here."
                }
            } else {
                viewBind.pullToRefresh.visibility = View.VISIBLE
                viewBind.rlNoChecks.visibility = View.GONE
                list.addAll(data)
                adapter.notifyDataSetChanged()
                hasMoreData = currentPage < resp.lastIndex
                currentPage++
            }
        }
    }

    private fun setScrollListener() {
        viewBind.rvChecks.addOnScrollListener(object :
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
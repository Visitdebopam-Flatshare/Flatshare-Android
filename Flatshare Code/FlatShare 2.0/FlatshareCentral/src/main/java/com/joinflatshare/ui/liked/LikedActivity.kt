package com.joinflatshare.ui.liked

import android.os.Bundle
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityLikedBinding
import com.joinflatshare.ui.base.BaseActivity

class LikedActivity : BaseActivity() {
    lateinit var viewBinding: ActivityLikedBinding
    lateinit var apiController: LikedApiController
    val flats = ArrayList<com.joinflatshare.pojo.explore.FlatRecommendationItem>()
    val users = ArrayList<com.joinflatshare.pojo.explore.UserRecommendationItem>()
    var SEARCH_TYPE = ""
    val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
    var viewTypeReceivedLikes = false
    val rvScrollStopListener = object : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING;
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLikedBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        showTopBar(this, true, "Checks", 0, 0)
        LikedListener(this, viewBinding)
        apiController = LikedApiController(this, viewBinding)
        initMenu()
    }

    private fun initMenu() {
        SEARCH_TYPE = TYPE_FHT
        /*if (AppConstants.loggedInUser?.isFlatSearch?.value == true) {
            SEARCH_TYPE = TYPE_FLAT
        } else {
            if (flat?.isMateSearch?.value == true)
                SEARCH_TYPE = TYPE_USER
        }*/
        viewBinding.llLikedReceived.performClick()
    }
}
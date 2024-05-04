package com.joinflatshare.ui.explore

import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.PaginationScrollListener
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.adapter.ExploreAdapter


class ExploreBinder(
    private val activity: ExploreActivity,
) {
    private val viewBind = activity.viewBind
    private val layoutManager: LinearLayoutManager
    lateinit var adapter: ExploreAdapter
    val rvScrollStopListener = object : SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING;
        }
    }

    init {
        viewBind.rvExplore.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        viewBind.rvExplore.layoutManager = layoutManager
        activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
        hideAll()
        initAdapter()
        setScrollListener()
    }

    private fun initAdapter() {
        adapter =
            ExploreAdapter(activity, activity.SEARCH_TYPE, activity.flat)
        viewBind.rvExplore.adapter = adapter
    }

    fun hideAll() {
        viewBind.llSearchOnHolder.visibility = View.GONE
        viewBind.llSearchOffHolder.visibility = View.GONE
        viewBind.llEmptyFlat.visibility = View.GONE
        viewBind.pullToRefresh.visibility = View.GONE
        viewBind.txtExploreMessage.visibility = View.GONE
        activity.errorMessage = ""
    }

    private fun allSearchOff() {
        viewBind.llSearchOnHolder.visibility = View.GONE
        viewBind.llSearchOffHolder.visibility = View.VISIBLE
        // check if user has a flat
        val myFlat = FlatShareApplication.getDbInstance().userDao().getFlatData()
        if (myFlat == null) {
            viewBind.txtExploreFlatButton.text = "Add My\nFlat"
        } else viewBind.txtExploreFlatButton.text = "My Flat"
        setAppbarDrag(false)
    }

    private fun atLeastOneSearchOn() {
        viewBind.llSearchOnHolder.visibility = View.VISIBLE
        viewBind.llSearchOffHolder.visibility = View.GONE
        viewBind.llEmptyFlat.visibility = View.GONE
        viewBind.pullToRefresh.visibility = View.GONE
    }

    fun setup() {
        viewBind.rvExploreButtons.visibility = View.GONE
        activity.apiController?.hasMoreFHTData = true
        activity.apiController?.currentFHTPage = 0
        resetAllCardButtons()
        viewBind.cardExploreFhtSearch.visibility = View.VISIBLE
        atLeastOneSearchOn()
        setSearchButtonForFHT()
        animateTextButtonForFHT()
        activity.apiController?.getRecommendedFHTUsers()
    }

    /*fun setup() {
        activity.apiController?.hasMoreData = true
        activity.apiController?.currentPage = 0
        activity.apiController?.hasMoreFHTData = true
        activity.apiController?.currentFHTPage = 0
        resetAllCardButtons()
        resetAllViews()

        if (activity.SEARCH_TYPE.isNotEmpty()) {
            when (activity.SEARCH_TYPE) {
                BaseActivity.TYPE_FHT -> if (AppConstants.loggedInUser?.isFHTSearch?.value == false)
                    activity.SEARCH_TYPE = ""

                BaseActivity.TYPE_DATE -> if (AppConstants.loggedInUser?.isDateSearch?.value == false)
                    activity.SEARCH_TYPE = ""

                BaseActivity.TYPE_FLAT -> if (AppConstants.loggedInUser?.isFlatSearch?.value == false)
                    activity.SEARCH_TYPE = ""

                BaseActivity.TYPE_USER -> {
                    activity.flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                    if (activity.flat == null || activity.flat?.isMateSearch?.value == false)
                        activity.SEARCH_TYPE = ""
                }
            }
        }

        if (AppConstants.loggedInUser?.isFlatSearch?.value == true
            || AppConstants.loggedInUser?.isFHTSearch?.value == true
            || AppConstants.loggedInUser?.isDateSearch?.value == true
        ) {
            atLeastOneSearchOn()
            if (AppConstants.loggedInUser?.isFHTSearch?.value == true)
                setSearchButtonForFHT()
            if (AppConstants.loggedInUser?.isDateSearch?.value == true)
                setSearchButtonForDate()
            if (AppConstants.loggedInUser?.isFlatSearch?.value == true)
                setSearchButtonForSFS()
        } else if (activity.flat?.isMateSearch?.value == true) {
            atLeastOneSearchOn()
            activity.SEARCH_TYPE = BaseActivity.TYPE_USER
            calculateErrorMessage()
        } else {
            allSearchOff()
            return
        }
        initAdapter()
        when (activity.SEARCH_TYPE) {
            BaseActivity.TYPE_FHT -> {
                animateTextButtonForFHT()
                activity.apiController?.getRecommendedFHTUsers()
                viewBind.rvExploreButtons.scrollToPosition(1)
            }

            BaseActivity.TYPE_DATE -> {
                animateTextButtonForDate()
                activity.apiController?.getRecommendedDateUsers()
                viewBind.rvExploreButtons.scrollToPosition(2)
            }

            BaseActivity.TYPE_FLAT -> {
                animateTextButtonForSFS()
                activity.apiController?.getRecommendedFlats()
                viewBind.rvExploreButtons.scrollToPosition(0)
            }

            BaseActivity.TYPE_USER -> {
                animateTextButtonForSFS()
                setSearchButtonForFlat()
                activity.apiController?.getRecommendedUsers()
                viewBind.rvExploreButtons.scrollToPosition(0)
            }
        }
    }*/

    private fun resetAllCardButtons() {
        viewBind.cardExploreOpenSearch.visibility = View.GONE
        viewBind.cardExploreDateSearch.visibility = View.GONE
        viewBind.cardExploreFhtSearch.visibility = View.GONE
    }

    fun resetAllViews() {
        viewBind.cardExploreOpenSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_bg
            )
        )
        viewBind.cardExploreOpenSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_icon
        )
        viewBind.txtFlatSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.imgFlatSearch.visibility = View.GONE

        // FHT
        viewBind.cardExploreFhtSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_bg
            )
        )
        viewBind.cardExploreFhtSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_icon
        )
        viewBind.txtFhtSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.imgFhtSearch.visibility = View.GONE

        // DATE
        viewBind.cardExploreDateSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_bg
            )
        )
        viewBind.cardExploreDateSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_icon
        )
        viewBind.txtDateSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.imgDateSearch.visibility = View.GONE
    }

    private fun setSearchButtonForFlat() {
        viewBind.cardExploreOpenSearch.visibility = View.VISIBLE
        viewBind.txtFlatSearch.text = "Flatmate"
    }

    private fun setSearchButtonForSFS() {
        viewBind.cardExploreOpenSearch.visibility = View.VISIBLE
        viewBind.txtFlatSearch.text = "Shared Flat"
        if (activity.SEARCH_TYPE.isEmpty())
            activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT
    }

    private fun setSearchButtonForFHT() {
        viewBind.cardExploreFhtSearch.visibility = View.VISIBLE
        if (activity.SEARCH_TYPE.isEmpty())
            activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
    }

    private fun setSearchButtonForDate() {
        viewBind.cardExploreDateSearch.visibility = View.VISIBLE
        if (activity.SEARCH_TYPE.isEmpty())
            activity.SEARCH_TYPE = BaseActivity.TYPE_DATE
    }

    fun animateTextButtonForFHT() {
        viewBind.cardExploreFhtSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_blue_light
            )
        )
        viewBind.cardExploreFhtSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_blue_light
        )
        viewBind.txtFhtSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.black
            )
        )
        viewBind.imgFhtSearch.visibility = View.VISIBLE
        viewBind.imgFhtSearch.setImageResource(R.drawable.ic_search_black_total)
    }

    fun animateTextButtonForSFS() {
        viewBind.cardExploreOpenSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_blue_light
            )
        )
        viewBind.cardExploreOpenSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_blue_light
        )
        viewBind.txtFlatSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.black
            )
        )
        viewBind.imgFlatSearch.visibility = View.VISIBLE
        viewBind.imgFlatSearch.setImageResource(R.drawable.ic_search_black_total)
    }

    fun animateTextButtonForDate() {
        viewBind.cardExploreDateSearch.setCardBackgroundColor(
            ContextCompat.getColor(
                activity,
                R.color.color_blue_light
            )
        )
        viewBind.cardExploreDateSearch.strokeColor = ContextCompat.getColor(
            activity,
            R.color.color_blue_light
        )
        viewBind.txtDateSearch.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.black
            )
        )
        viewBind.imgDateSearch.visibility = View.VISIBLE
        viewBind.imgDateSearch.setImageResource(R.drawable.ic_search_black_total)
    }

    fun showEmptyData(text: String) {
        viewBind.llEmptyFlat.visibility = View.VISIBLE
        viewBind.imgExploreEmpty.setImageResource(R.drawable.ic_runout)
        if (text.isNotEmpty()) viewBind.txtFlatEmpty.text = text
        activity.binder.setAppbarDrag(false)
    }

    fun showContentData() {
        viewBind.llEmptyFlat.visibility = View.GONE
        viewBind.pullToRefresh.visibility = View.VISIBLE
        activity.binder.adapter.notifyDataSetChanged()
        activity.binder.setAppbarDrag(true)
    }

    private fun setScrollListener() {
        viewBind.rvExplore.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                if (activity.SEARCH_TYPE == BaseActivity.TYPE_FHT)
                    activity.apiController?.getRecommendedFHTUsers()
                else if (activity.SEARCH_TYPE == BaseActivity.TYPE_USER)
                    activity.apiController?.getRecommendedUsers()
                else if (activity.SEARCH_TYPE == BaseActivity.TYPE_FLAT)
                    activity.apiController?.getRecommendedFlats()
            }

            override fun isLastPage(): Boolean {
                if (activity.SEARCH_TYPE == BaseActivity.TYPE_FHT)
                    return activity.apiController?.hasMoreFHTData == false
                else
                    return activity.apiController?.hasMoreData == false
            }

            override fun isLoading(): Boolean {
                return activity.apiController?.isDataFetching == true
            }
        })
    }

    private fun setAppbarDrag(canScroll: Boolean) {
        if (viewBind.appBarLayout.layoutParams != null) {
            val layoutParams: CoordinatorLayout.LayoutParams =
                viewBind.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
            val appBarLayoutBehaviour = AppBarLayout.Behavior()
            appBarLayoutBehaviour.setDragCallback(object : DragCallback() {
                override fun canDrag(@NonNull appBarLayout: AppBarLayout): Boolean {
                    return canScroll
                }
            })
            layoutParams.behavior = appBarLayoutBehaviour
        }
    }

    private fun calculateErrorMessage() {
        if (activity.flat?.images.isNullOrEmpty())
            activity.errorMessage = "Add Flat Photos"
        else {
            val properties = activity.flat?.flatProperties
            if (properties != null) {
                if (properties.flatsize.isNullOrBlank() || properties.roomType.isNullOrBlank())
                    activity.errorMessage = "Add Flat Type & Room Type"
                else if (properties.furnishing.isNullOrEmpty() || properties.amenities.isNullOrEmpty())
                    activity.errorMessage = "Add Furnishing & Amenities"
                else {
                    if (properties.gender.isNullOrBlank()
                        || properties.moveinDate.isNullOrEmpty()
                        || properties.rentperPerson == 0
                        || properties.depositperPerson == 0
                    )
                        activity.errorMessage = "Complete Flatmate Search Details"
                }
            }
        }
        if (activity.errorMessage.isNotEmpty()) {
            viewBind.txtExploreMessage.text = activity.errorMessage
            viewBind.txtExploreMessage.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.red
                )
            )
            viewBind.txtExploreMessage.visibility = View.VISIBLE
        }
    }

}
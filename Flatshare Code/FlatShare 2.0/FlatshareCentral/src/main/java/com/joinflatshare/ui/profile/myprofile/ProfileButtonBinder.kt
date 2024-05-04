package com.joinflatshare.ui.profile.myprofile

import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityExploreBinding
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity

class ProfileButtonBinder(
    private val activity: BaseActivity,
    private val binder: ViewBinding
) {
    private val buttons = ArrayList<ModelBottomSheet>()
    private var adapter: ProfileAdapter? = null
    private var itemDecoration: GridSpacingItemDecoration? = null

    private fun setRecyclerView() {
        // calculate Buttons
        buttons.clear()
        val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
        val allSearchesTurnedOff =
            (AppConstants.loggedInUser?.isFlatSearch?.value == false)
                    && (AppConstants.loggedInUser?.isFHTSearch?.value == false)
                    && (AppConstants.loggedInUser?.isDateSearch?.value == false)
                    && (flat == null || !flat.isMateSearch.value)
        if (activity is ExploreActivity) {
            if (allSearchesTurnedOff) {
                buttons.add(
                    ModelBottomSheet(
                        R.drawable.ic_shared_flat, "Shared Flat\nSearch",
                        1
                    )
                )
                buttons.add(
                    ModelBottomSheet(
                        R.drawable.ic_flat, "My Flat",
                        2
                    )
                )
                buttons.add(
                    ModelBottomSheet(
                        R.drawable.ic_fht, "Flathunt\nTogether",
                        7
                    )
                )
                buttons.add(
                    ModelBottomSheet(
                        R.drawable.ic_date, "Vibe Check",
                        8
                    )
                )
            } else {
                if (AppConstants.loggedInUser?.isFlatSearch?.value == true
                    || AppConstants.loggedInUser?.isFHTSearch?.value == true
                    || AppConstants.loggedInUser?.isDateSearch?.value == true
                ) {
                    buttons.add(
                        ModelBottomSheet(
                            R.drawable.ic_shared_flat, "Shared Flat\nSearch",
                            1
                        )
                    )
                    buttons.add(
                        ModelBottomSheet(
                            R.drawable.ic_fht, "Flathunt\nTogether",
                            7
                        )
                    )
                    buttons.add(
                        ModelBottomSheet(
                            R.drawable.ic_date, "Vibe Check",
                            8
                        )
                    )
                } else if (flat?.isMateSearch?.value == true) {
                    buttons.add(
                        ModelBottomSheet(
                            R.drawable.ic_flat, "My Flat",
                            2
                        )
                    )
                }
            }
        } else {
            buttons.add(
                ModelBottomSheet(
                    R.drawable.ic_check, "Checks",
                    3
                )
            )
            buttons.add(
                ModelBottomSheet(
                    R.drawable.ic_chat_request, "Chat\nRequests",
                    4
                )
            )
            buttons.add(
                ModelBottomSheet(
                    R.drawable.ic_friend, "Friends",
                    5
                )
            )
            buttons.add(
                ModelBottomSheet(
                    R.drawable.ic_settings, "FAQs &\nSettings",
                    6
                )
            )
        }
    }

    fun setAdapter() {
        setRecyclerView()
        val spanCount = 2
        val spacing = 25
        val includeEdge = false
        if (itemDecoration == null) {
            itemDecoration = GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        }

        if (activity is ProfileActivity && binder is ActivityProfileBinding) {
            // set recyclerview
            binder.rvProfileButtons.removeItemDecoration(itemDecoration!!)
            binder.rvProfileButtons.addItemDecoration(itemDecoration!!)
            binder.rvProfileButtons.layoutManager = GridLayoutManager(activity, spanCount)
            if (adapter == null) {
                adapter = ProfileAdapter(activity, buttons, 0)
                binder.rvProfileButtons.adapter = adapter
            } else adapter?.notifyDataSetChanged()
        } else if (activity is ExploreActivity && binder is ActivityExploreBinding) {

        }
    }
}
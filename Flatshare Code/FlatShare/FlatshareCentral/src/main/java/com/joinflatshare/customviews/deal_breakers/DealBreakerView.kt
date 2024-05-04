package com.joinflatshare.customviews.deal_breakers

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.pojo.user.DateProperties
import com.joinflatshare.pojo.user.FlatProperties
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 17/12/22
 */
class DealBreakerView(val activity: BaseActivity, private val recyclerView: RecyclerView) {
    private val items = ArrayList<ModelBottomSheet>()
    private var callback: DealBreakerCallback? = null
    private var smoking = 0
    private var nonVeg = 0
    private var party = 0
    private var eggs = 0
    private var workout = 0
    private var pets = 0
    private var adapter: DealBreakerAdapter? = null

    fun assignCallback(callback: DealBreakerCallback) {
        this.callback = callback
    }

    fun setDealValues(properties: FlatProperties?, view: View?) {
        if (isViewVisible(properties?.dealBreakers)) {
            val dealBreakers = properties?.dealBreakers!!
            this.smoking = dealBreakers.smoking
            this.nonVeg = dealBreakers.nonveg
            this.party = dealBreakers.flatparty
            this.eggs = dealBreakers.eggs
            this.workout = dealBreakers.workout
            this.pets = dealBreakers.pets
            view?.visibility = View.VISIBLE
        } else {
            view?.visibility = if (callback == null) View.GONE else View.VISIBLE
        }
    }

    fun setDealValues(properties: DateProperties?, view: View?) {
        if (isViewVisible(properties?.dealBreakers)) {
            val dealBreakers = properties?.dealBreakers!!
            this.smoking = dealBreakers.smoking
            this.nonVeg = dealBreakers.nonveg
            this.party = dealBreakers.flatparty
            this.eggs = dealBreakers.eggs
            this.workout = dealBreakers.workout
            this.pets = dealBreakers.pets
            view?.visibility = View.VISIBLE
        } else {
            view?.visibility = if (callback == null) View.GONE else View.VISIBLE
        }
    }

    fun setDealValues(properties: com.joinflatshare.pojo.flat.FlatProperties?, view: View?) {
        if (isViewVisible(properties?.dealBreakers)) {
            val dealBreakers = properties?.dealBreakers!!
            this.smoking = dealBreakers.smoking
            this.nonVeg = dealBreakers.nonveg
            this.party = dealBreakers.flatparty
            this.eggs = dealBreakers.eggs
            this.workout = dealBreakers.workout
            this.pets = dealBreakers.pets
            view?.visibility = View.VISIBLE
        } else {
            view?.visibility = if (callback == null) View.GONE else View.VISIBLE
        }
    }

    private fun isViewVisible(dealBreakers: DealBreakers?): Boolean {
        return dealBreakers != null && !areAllDealBreakersEmpty(dealBreakers)
    }


    fun show() {
        if (callback == null) {
            // Only viewable. Set as FlowLayoutManager
            val layoutManager = FlexboxLayoutManager(activity)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            recyclerView.layoutManager = layoutManager

            // Set Values
            if (smoking > 0) items.add(ModelBottomSheet(R.drawable.ic_smoking, "Smoking"))
            if (nonVeg > 0) items.add(ModelBottomSheet(R.drawable.ic_non_veg, "Eating Non-Veg"))
            if (party > 0) items.add(ModelBottomSheet(R.drawable.ic_flat_party, "Drinking Alcohol"))
            if (eggs > 0) items.add(ModelBottomSheet(R.drawable.ic_eggs, "Eating Eggs"))
            if (workout > 0) items.add(ModelBottomSheet(R.drawable.ic_workout, "Workout"))
            if (pets > 0) items.add(ModelBottomSheet(R.drawable.ic_pets, "Pets"))
        } else {
            if (recyclerView.layoutManager == null) {
                // Editable as well. Set linear layout Manager
                recyclerView.layoutManager = GridLayoutManager(activity, 2)
                recyclerView.addItemDecoration(
                    GridSpacingItemDecoration(
                        2, 10, false
                    )
                )
                // Set Values
                items.add(ModelBottomSheet(R.drawable.ic_smoking, "Smoking"))
                items.add(ModelBottomSheet(R.drawable.ic_non_veg, "Eating Non-Veg"))
                items.add(ModelBottomSheet(R.drawable.ic_flat_party, "Drinking Alcohol"))
                items.add(ModelBottomSheet(R.drawable.ic_eggs, "Eating Eggs"))
                items.add(ModelBottomSheet(R.drawable.ic_workout, "Workout"))
                items.add(ModelBottomSheet(R.drawable.ic_pets, "Pets"))
            }

        }
        if (adapter == null) {
            adapter =
                DealBreakerAdapter(
                    this,
                    items,
                    smoking,
                    nonVeg,
                    party,
                    eggs,
                    workout,
                    pets,
                    callback
                )
            recyclerView.adapter = adapter
        } else adapter?.notifyDataSetChanged()

    }

    companion object {
        fun areAllDealBreakersEmpty(dealBreakers: DealBreakers?): Boolean {
            return (dealBreakers?.smoking == 0 &&
                    dealBreakers.nonveg == 0 &&
                    dealBreakers.flatparty == 0 &&
                    dealBreakers.eggs == 0 &&
                    dealBreakers.workout == 0 &&
                    dealBreakers.pets == 0
                    )
        }
    }
}
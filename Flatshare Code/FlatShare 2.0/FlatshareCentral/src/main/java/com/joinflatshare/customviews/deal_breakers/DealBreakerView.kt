package com.joinflatshare.customviews.deal_breakers

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.base.ApplicationBaseActivity

/**
 * Created by debopam on 17/12/22
 */
class DealBreakerView(
    val activity: ApplicationBaseActivity,
    private val recyclerView: RecyclerView
) {
    private val items = ArrayList<ModelBottomSheet>()
    private var isEditable: Boolean = false
    private lateinit var dealBreakers: DealBreakers
    private var adapter: DealBreakerAdapter? = null
    private var callback: DealBreakerCallback? = null

    fun assignCallback(callback: DealBreakerCallback) {
        this.callback = callback
    }

    fun getDealBreakers(): DealBreakers {
        return if (adapter == null)
            dealBreakers
        else
            adapter!!.dealBreakers
    }

    fun isEditable(shouldEdit: Boolean) {
        isEditable = shouldEdit
    }

    fun setDealValues(dealBreakers: DealBreakers?, view: View?) {
        if (!isViewVisible(dealBreakers))
            this.dealBreakers = DealBreakers()
        else this.dealBreakers = dealBreakers!!
        view?.visibility = View.VISIBLE
    }

    private fun isViewVisible(dealBreakers: DealBreakers?): Boolean {
        return dealBreakers != null && !areAllDealBreakersEmpty(dealBreakers)
    }


    fun show() {
        if (!isEditable) {
            // Only viewable. Set as FlowLayoutManager
            val layoutManager = FlexboxLayoutManager(activity)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            recyclerView.layoutManager = layoutManager

            // Set Values
            if (dealBreakers.smoking == 1 || dealBreakers.smoking == 2) items.add(
                ModelBottomSheet(
                    "Smoking",
                    dealBreakers.smoking
                )
            )
            if (dealBreakers.nonveg == 1 || dealBreakers.nonveg == 2) items.add(
                ModelBottomSheet(
                    "Eating Non-Veg",
                    dealBreakers.nonveg
                )
            )
            if (dealBreakers.flatparty == 1 || dealBreakers.flatparty == 2) items.add(
                ModelBottomSheet("Drinking Alcohol", dealBreakers.flatparty)
            )
            if (dealBreakers.eggs == 1 || dealBreakers.eggs == 2) items.add(
                ModelBottomSheet(
                    "Eating Eggs",
                    dealBreakers.eggs
                )
            )
            if (dealBreakers.workout == 1 || dealBreakers.workout == 2) items.add(
                ModelBottomSheet(
                    "Workout",
                    dealBreakers.workout
                )
            )
            if (dealBreakers.pets == 1 || dealBreakers.pets == 2) items.add(
                ModelBottomSheet(
                    "Pets",
                    dealBreakers.pets
                )
            )
        } else {
            if (recyclerView.layoutManager == null) {
                // Editable as well. Set linear layout Manager
                recyclerView.layoutManager = LinearLayoutManager(activity)
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
                    dealBreakers, items, isEditable, callback
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
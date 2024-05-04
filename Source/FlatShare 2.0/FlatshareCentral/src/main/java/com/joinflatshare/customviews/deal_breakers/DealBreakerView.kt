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
import com.joinflatshare.pojo.user.DateProperties
import com.joinflatshare.pojo.user.FlatProperties
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 17/12/22
 */
class DealBreakerView(val activity: BaseActivity, private val recyclerView: RecyclerView) {
    private val items = ArrayList<ModelBottomSheet>()
    private var isEditable: Boolean = false
    private lateinit var dealBreakers: DealBreakers
    private var adapter: DealBreakerAdapter? = null

    @Deprecated("Removed")
    fun assignCallback(callback: DealBreakerCallback) {
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

    @Deprecated("remove")
    fun setDealValues(dealBreakers: FlatProperties?, view: View?) {
       //TODO remove
    }

    fun setDealValues(dealBreakers: DealBreakers?, view: View?) {
        if (!isViewVisible(dealBreakers))
            this.dealBreakers = DealBreakers()
        else this.dealBreakers = dealBreakers!!
        view?.visibility = View.VISIBLE
    }

    @Deprecated("Removed")
    fun setDealValues(properties: DateProperties?, view: View?) {

    }

    @Deprecated("Removed")
    fun setDealValues(properties: com.joinflatshare.pojo.flat.FlatProperties?, view: View?) {

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
            if (dealBreakers.smoking > 0) items.add(ModelBottomSheet("Smoking", 1))
            if (dealBreakers.nonveg > 0) items.add(ModelBottomSheet("Eating Non-Veg", 1))
            if (dealBreakers.flatparty > 0) items.add(ModelBottomSheet("Drinking Alcohol", 1))
            if (dealBreakers.eggs > 0) items.add(ModelBottomSheet("Eating Eggs", 1))
            if (dealBreakers.workout > 0) items.add(ModelBottomSheet("Workout", 1))
            if (dealBreakers.pets > 0) items.add(ModelBottomSheet("Pets", 1))
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
                    dealBreakers, items, isEditable
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
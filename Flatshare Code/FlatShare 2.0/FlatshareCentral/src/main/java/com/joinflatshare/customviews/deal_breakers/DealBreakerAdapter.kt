package com.joinflatshare.customviews.deal_breakers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemDealBreakerBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemDealBreakerViewBinding
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.utils.helper.CommonMethod

class DealBreakerAdapter(
    private val dealBreaker: DealBreakerView,
    val dealBreakers: DealBreakers,
    val items: ArrayList<ModelBottomSheet>,
    private var isEditable: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind = if (isEditable)
            ItemDealBreakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        else ItemDealBreakerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(dealBreaker, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(
        private val dealBreaker: DealBreakerView,
        private val view: ViewBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: DealBreakerAdapter
        ) {
            val item = adapter.items[position]
            if (adapter.isEditable) {
                val holder = view as ItemDealBreakerBinding
                // Set Layout Params
                val linearParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                holder.llDealWrapper.layoutParams = linearParams
                var value = 0
                when (item.name) {
                    "Smoking" -> value = adapter.dealBreakers.smoking
                    "Eating Non-Veg" -> value = adapter.dealBreakers.nonveg
                    "Drinking Alcohol" -> value = adapter.dealBreakers.flatparty
                    "Eating Eggs" -> value = adapter.dealBreakers.eggs
                    "Workout" -> value = adapter.dealBreakers.workout
                    "Pets" -> value = adapter.dealBreakers.pets
                }

                holder.txtDeal.text = item.name
                holder.rlDealHolder.background = ContextCompat.getDrawable(
                    dealBreaker.activity,
                    R.drawable.drawable_button_grey_stroke
                )
                holder.txtDeal.setTextColor(
                    ContextCompat.getColor(
                        dealBreaker.activity,
                        R.color.grey2
                    )
                )
                holder.imgDealTick.setImageResource(R.drawable.ic_tick)
                holder.imgDealUntick.setImageResource(R.drawable.ic_cross_circle)


                CommonMethod.makeLog("Value", "" + value)

                if (value == 0) {
                    holder.imgDealTick.setColorFilter(0)
                    holder.imgDealUntick.setColorFilter(0)
                }
                else if (value == 2) {
                    // Untick selected
                    holder.rlDealHolder.background = ContextCompat.getDrawable(
                        dealBreaker.activity,
                        R.drawable.drawable_button_red_stroke_red_bg
                    )
                    holder.txtDeal.setTextColor(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.red
                        )
                    )
                    holder.imgDealTick.setColorFilter(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.red
                        )
                    )
                    holder.imgDealUntick.setColorFilter(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.red
                        )
                    )
                } else if (value == 1) {
                    // Tick Selected
                    holder.rlDealHolder.background = ContextCompat.getDrawable(
                        dealBreaker.activity,
                        R.drawable.drawable_button_blue_stroke_blue_bg
                    )
                    holder.txtDeal.setTextColor(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.blue_dark
                        )
                    )
                    holder.imgDealTick.setColorFilter(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.blue_dark
                        )
                    )
                    holder.imgDealUntick.setColorFilter(
                        ContextCompat.getColor(
                            dealBreaker.activity,
                            R.color.blue_dark
                        )
                    )
                }
                holder.imgDealTick.setOnClickListener {
                    setDealValue(position, item, 1, adapter, true)
                }
                holder.imgDealUntick.setOnClickListener {
                    setDealValue(position, item, 2, adapter, false)
                }
            } else {
                val holder = view as ItemDealBreakerViewBinding
                holder.imgLanguage.visibility = View.VISIBLE
                holder.imgLanguage.setImageResource(
                    if (item.type == 1) R.drawable.ic_tick_blue_fat
                    else R.drawable.ic_cross_red
                )
                holder.imgLanguage.setColorFilter(
                    ContextCompat.getColor(
                        adapter.dealBreaker.activity,
                        R.color.white
                    )
                )
                holder.txtAmenity.text = item.name
            }
        }

        private fun setDealValue(
            position: Int,
            item: ModelBottomSheet,
            value: Int,
            adapter: DealBreakerAdapter,
            isTickSelected: Boolean
        ) {
            when (item.name) {
                "Smoking" -> {
                    adapter.dealBreakers.smoking =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.smoking == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.smoking == 2) 0 else value
                        }

                }

                "Eating Non-Veg" -> {
                    adapter.dealBreakers.nonveg =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.nonveg == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.nonveg == 2) 0 else value
                        }
                }

                "Drinking Alcohol" -> {
                    adapter.dealBreakers.flatparty =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.flatparty == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.flatparty == 2) 0 else value
                        }
                }

                "Eating Eggs" -> {
                    adapter.dealBreakers.eggs =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.eggs == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.eggs == 2) 0 else value
                        }
                }

                "Workout" -> {
                    adapter.dealBreakers.workout =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.workout == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.workout == 2) 0 else value
                        }
                }

                "Pets" -> {
                    adapter.dealBreakers.pets =
                        if (isTickSelected) {
                            if (adapter.dealBreakers.pets == 1) 0 else value
                        } else {
                            if (adapter.dealBreakers.pets == 2) 0 else value
                        }
                }
            }

            adapter.notifyItemChanged(position)
        }
    }
}
package com.joinflatshare.customviews.deal_breakers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ItemDealBreakerBinding
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.ui.dialogs.DialogSmoking

class DealBreakerAdapter(
    private val dealBreaker: DealBreakerView,
    val items: ArrayList<ModelBottomSheet>,
    private var smoking: Int,
    private var nonVeg: Int,
    private var party: Int,
    private var eggs: Int,
    private var workout: Int,
    private var pets: Int,
    private var callback: DealBreakerCallback?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemDealBreakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val holder: ItemDealBreakerBinding
    ) :
        RecyclerView.ViewHolder(holder.root) {
        fun bind(
            position: Int,
            adapter: DealBreakerAdapter
        ) {
            // Set Layout Params
            val linearParams: LinearLayout.LayoutParams
            if (adapter.callback == null) {
                val params = FlexboxLayoutManager.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                holder.viewDealsSpaceHorizontal.visibility = View.VISIBLE
                holder.viewDealsSpaceVertical.visibility = View.VISIBLE
                holder.llDealHolder.layoutParams = params
            } else {
                val params = FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                holder.viewDealsSpaceHorizontal.visibility = View.GONE
                holder.viewDealsSpaceVertical.visibility = View.GONE
                holder.llDealHolder.layoutParams = params
            }
            holder.llDealWrapper.layoutParams = linearParams

            val item = adapter.items[position]
            var value = 0
            when (item.name) {
                "Smoking" -> value = adapter.smoking
                "Eating Non-Veg" -> value = adapter.nonVeg
                "Drinking Alcohol" -> value = adapter.party
                "Eating Eggs" -> value = adapter.eggs
                "Workout" -> value = adapter.workout
                "Pets" -> value = adapter.pets
            }

            holder.txtDeal.text = item.name
            if (value == 0) {
                holder.imgDeal.setImageResource(item.icon)
                holder.imgDeal.visibility = View.VISIBLE
                holder.txtDealEmoji.visibility = View.GONE
            } else {
                holder.imgDeal.visibility = View.GONE
                holder.txtDealEmoji.visibility = View.VISIBLE
                when (value) {
                    1 -> holder.txtDealEmoji.text = dealBreaker.activity.getEmojiByUnicode(0x1F6AB)
                    2 -> holder.txtDealEmoji.text = dealBreaker.activity.getEmojiByUnicode(0x1F44D)
                    3 -> holder.txtDealEmoji.text = dealBreaker.activity.getEmojiByUnicode(0x1F60D)
                }
            }

            if (adapter.callback != null) {
                holder.cardDeal.setOnClickListener {
                    DialogSmoking(
                        dealBreaker.activity,
                        holder.cardDeal,
                        holder.llDealContent
                    ) { text ->
                        val emojiValue = Integer.parseInt(text)
                        when (position) {
                            0 -> {
                                adapter.callback?.onSmokingSelected(emojiValue)
                                adapter.smoking = emojiValue
                            }
                            1 -> {
                                adapter.callback?.onNonVegSelected(emojiValue)
                                adapter.nonVeg = emojiValue
                            }
                            2 -> {
                                adapter.callback?.onPartySelected(emojiValue)
                                adapter.party = emojiValue
                            }
                            3 -> {
                                adapter.callback?.onEggsSelected(emojiValue)
                                adapter.eggs = emojiValue
                            }
                            4 -> {
                                adapter.callback?.onWorkoutSelected(emojiValue)
                                adapter.workout = emojiValue
                            }
                            5 -> {
                                adapter.callback?.onPetsSelected(emojiValue)
                                adapter.pets = emojiValue
                            }
                        }
                        adapter.notifyItemChanged(position)
                    }
                }
            }

        }
    }
}
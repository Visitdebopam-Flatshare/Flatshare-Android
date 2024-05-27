package com.joinflatshare.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemFaqBinding
import com.joinflatshare.pojo.faq.FaqItem

class FaqAdapter(
    private val items: ArrayList<FaqItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val view: ItemFaqBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: FaqAdapter
        ) {
            val item = adapter.items[position]
            if (item.isQuestion) {
                view.txtFaqHeader.visibility = View.VISIBLE
                view.txtFaqDesc.visibility = View.GONE
                view.gap.visibility = View.GONE
                view.txtFaqHeader.text = item.question
            } else {
                view.txtFaqHeader.visibility = View.GONE
                view.txtFaqDesc.visibility = View.VISIBLE
                view.gap.visibility = View.GONE
                view.txtFaqDesc.text = item.question

                if (position == adapter.itemCount - 1)
                    view.gap.visibility = View.VISIBLE
                else {
                    if (adapter.items[position + 1].isQuestion)
                        view.gap.visibility = View.VISIBLE
                    else view.gap.visibility = View.GONE
                }

            }
        }
    }
}
package com.joinflatshare.ui.flat.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemAmenitiesBinding

class FlatEditAmenityAdapter(
    private val items: List<String>,
    val selectedAmenityList: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemAmenitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.adapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val view: ItemAmenitiesBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: FlatEditAmenityAdapter
        ) {
            val amenity = adapter.items[position]
            view.txtBeds.text = amenity
            view.ckbAmenities.isChecked = adapter.selectedAmenityList.contains(amenity)
            view.ckbAmenities.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    adapter.selectedAmenityList.add(amenity)
                } else {
                    if (adapter.selectedAmenityList.contains(amenity))
                        adapter.selectedAmenityList.remove(amenity)
                }

            }
        }
    }
}
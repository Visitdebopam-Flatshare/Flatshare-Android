package com.joinflatshare.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemAdminFeatureBinding
import com.joinflatshare.ui.admin.features.DeleteUnregisteredUsersFromSendbird
import com.joinflatshare.ui.admin.features.FlatshareUsersTableReload
import com.joinflatshare.ui.admin.features.SendBirdUsersTableReload
import com.joinflatshare.ui.admin.features.SendbirdChannelFix
import com.joinflatshare.ui.admin.features.SendbirdUserNameFix
import com.joinflatshare.utils.helper.CommonMethod

class AdminFeatureAdapter(
    private val activity: AdminFeatureActivity,
    private val items: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemAdminFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val view: ItemAdminFeatureBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            adapter: AdminFeatureAdapter
        ) {
            val item = adapter.items[position]
            view.txtAdminHeader.text = item
            view.txtAdminHeader.setOnClickListener {
                CommonMethod.makeToast("Clicked on $item")
                when (item) {
                    "Reload Sendbird Users" -> {
                        SendBirdUsersTableReload().reload(adapter.activity)
                    }

                    "Reload Flatshare Users" -> {
                        FlatshareUsersTableReload().reload(adapter.activity)
                    }

                    "Sendbird User Not Registered" -> {
                        SendbirdChannelFix().fix(adapter.activity)
                    }

                    "Sendbird User Name Fix" -> {
                        SendbirdUserNameFix().fix(adapter.activity)
                    }

                    "Sendbird Delete Unregistered Users" -> {
                        DeleteUnregisteredUsersFromSendbird().deleteUnregisteredUsers(adapter.activity)
                    }
                }
            }
        }
    }
}
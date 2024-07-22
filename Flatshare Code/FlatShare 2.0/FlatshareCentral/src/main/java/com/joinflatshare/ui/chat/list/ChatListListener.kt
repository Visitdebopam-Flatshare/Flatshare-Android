package com.joinflatshare.ui.chat.list

import android.text.Editable
import android.text.TextWatcher
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatListBinding
import java.util.Locale

class ChatListListener(
    private val activity: ChatListActivity,
    private val viewBind: ActivityChatListBinding
) {
    init {
        searchList()
    }

    private fun searchList() {
        viewBind.edtChatSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (activity.allGroupChannelList.size > 0) {
                    activity.searchedGroupChannelList.clear()
                    val query = editable.toString()
                    if (query.isEmpty()) {
                        activity.searchedGroupChannelList.addAll(activity.allGroupChannelList)
                        activity.adapter?.notifyDataSetChanged()
                    } else {
                        for (temp in activity.allGroupChannelList) {
                            for (member in temp.members) {
                                if (member.nickname.lowercase(Locale.getDefault()).contains(
                                        query.lowercase(
                                            Locale.getDefault()
                                        )
                                    )
                                ) {
                                    activity.searchedGroupChannelList.add(temp)
                                    break
                                }
                            }
                        }
                        activity.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
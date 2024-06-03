package com.joinflatshare.ui.checks.request_chat

import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityRequestChatBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet

class ChatRequestListener(
    private val activity: ChatRequestActivity,
    private val viewBind: ActivityRequestChatBinding
) : View.OnClickListener {
    init {
//        viewBind.cardChat.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.cardChat.id -> {
                val list = ArrayList<ModelBottomSheet>()
                val dao=FlatShareApplication.getDbInstance().requestDao()
                list.add(ModelBottomSheet(0, dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U),"Shared Flat Search"))
                list.add(ModelBottomSheet(0, dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT),"Flathunt Together"))
                list.add(ModelBottomSheet(0, dao.getCount(ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F),"Flatmate Search"))
                list.add(ModelBottomSheet(0, dao.getCount(""+ ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL),"Casual Date"))
                list.add(ModelBottomSheet(0, dao.getCount(""+ ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM),"Long-Term Partner"))
                list.add(ModelBottomSheet(0, dao.getCount(""+ ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS),"Activity Partners"))
                BottomSheetView(activity, list).show { view, position ->
                    val oldType = activity.type
                    when (position) {
                        0 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        1 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        2 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                        3 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL.toString()
                        4 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM.toString()
                        5 -> activity.type = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS.toString()
                    }
                    if (oldType != activity.type)
                        activity.getChatRequest()
                }
            }
        }
    }
}
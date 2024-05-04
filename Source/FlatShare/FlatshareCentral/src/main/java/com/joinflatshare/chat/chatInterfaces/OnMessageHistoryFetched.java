package com.joinflatshare.chat.chatInterfaces;

import com.sendbird.android.message.BaseMessage;

import java.util.List;

public interface OnMessageHistoryFetched {
    void onMessageFetched(List<? extends BaseMessage> list);
}

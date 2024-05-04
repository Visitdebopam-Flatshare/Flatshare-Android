package com.joinflatshare.chat.chatInterfaces;

import com.sendbird.android.message.UserMessage;

public interface OnTextMessageSentListener {
    void onTextMessageSent(UserMessage userMessage);
}

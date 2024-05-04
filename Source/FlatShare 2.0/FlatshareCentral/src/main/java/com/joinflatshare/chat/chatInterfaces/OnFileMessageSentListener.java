package com.joinflatshare.chat.chatInterfaces;

import com.sendbird.android.message.FileMessage;

public interface OnFileMessageSentListener {
    void onFileMessageSent(FileMessage userMessage);
}

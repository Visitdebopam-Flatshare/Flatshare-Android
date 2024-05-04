package com.joinflatshare.chat.chatInterfaces;

import com.sendbird.android.user.User;

import java.util.List;

public interface OnUserListFetchedListener {
    void onUsersFetched(List<? extends User> users);
}

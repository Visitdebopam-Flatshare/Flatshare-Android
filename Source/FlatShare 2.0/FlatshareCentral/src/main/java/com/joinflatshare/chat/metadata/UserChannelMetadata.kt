package com.joinflatshare.chat.metadata

import com.joinflatshare.pojo.user.User

class UserChannelMetadata : SendbirdMetadata() {
    var userMap = HashMap<String, User>()
}
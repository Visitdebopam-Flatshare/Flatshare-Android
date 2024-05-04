package com.joinflatshare.chat.metadata

import com.joinflatshare.pojo.user.ModelLocation

class UserMetadata : SendbirdMetadata() {
    var location = ModelLocation()
    var locationTime = 0L
}
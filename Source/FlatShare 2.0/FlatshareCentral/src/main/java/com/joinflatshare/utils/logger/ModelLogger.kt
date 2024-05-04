package com.joinflatshare.utils.logger

data class ModelLogger(
    var userId: String? = "",
    var timestamp: String? = "",
    var millis: Long = 0L,
    var reason: String? = "",
    var type: String? = ""
)

package com.joinflatshare.constants

import android.text.format.DateUtils

object SendBirdConstants {
    @JvmField
    var SENDBIRD_APPID = ""

    @JvmField
    var unreadChannelCount = 0

    // API
    @JvmField
    var SENDBIRD_BASEURL = ""

    @JvmField
    var SENDBIRD_API_TOKEN = ""

    fun setSendbirdBaseurl() {
        SENDBIRD_BASEURL = "https://api-$SENDBIRD_APPID.sendbird.com/v3/"
    }

    const val USER_STATUS_REFRESH_DELAY = DateUtils.SECOND_IN_MILLIS * 15

    // Channel Type
    @JvmField
    val CHANNEL_TYPE_CONNECTION_U2F = "CHANNEL_CONNECTION_U2F"

    @JvmField
    val CHANNEL_TYPE_CONNECTION_F2U = "CHANNEL_CONNECTION_F2U"

    @JvmField
    val CHANNEL_TYPE_CONNECTION_FHT = "CHANNEL_CONNECTION_FHT"

    @JvmField
    val CHANNEL_TYPE_CONNECTION_CASUAL = "CHANNEL_CONNECTION_CSU"

    @JvmField
    val CHANNEL_TYPE_CONNECTION_LONG_TERM = "CHANNEL_CONNECTION_LTR"

    @JvmField
    val CHANNEL_TYPE_CONNECTION_ACTIVITY_PARTNERS = "CHANNEL_CONNECTION_ACT"

    @JvmField
    val CHANNEL_TYPE_MATCH_U2F = "CHANNEL_MATCH_U2F"

    @JvmField
    val CHANNEL_TYPE_MATCH_F2U = "CHANNEL_MATCH_F2U"

    @JvmField
    val CHANNEL_TYPE_MATCH_FHT = "CHANNEL_MATCH_FHT"

    @JvmField
    val CHANNEL_TYPE_MATCH_CASUAL = "CHANNEL_MATCH_CSU"

    @JvmField
    val CHANNEL_TYPE_MATCH_LONG_TERM = "CHANNEL_MATCH_LTR"

    @JvmField
    val CHANNEL_TYPE_MATCH_ACTIVITY_PARTNERS = "CHANNEL_MATCH_ACT"

    @JvmField
    val CHANNEL_TYPE_FLAT = "CHANNEL_FLAT"

    @JvmField
    val CHANNEL_TYPE_FRIEND = "CHANNEL_FRIEND"

    // Message Type
    @JvmField
    val MESSAGE_TYPE_TEXT = "MESSAGE_TYPE_TEXT"

    @JvmField
    val MESSAGE_TYPE_LOCATION = "MESSAGE_TYPE_LOCATION"

    @JvmField
    val MESSAGE_TYPE_AUDIO = "MESSAGE_TYPE_AUDIO"

    @JvmField
    val MESSAGE_TYPE_IMAGE = "MESSAGE_TYPE_IMAGE"

    @JvmField
    val MESSAGE_TYPE_VIDEO = "MESSAGE_TYPE_VIDEO"

    @JvmField
    val MESSAGE_TYPE_CONTACT = "MESSAGE_TYPE_CONTACT"

    @JvmField
    val MESSAGE_TYPE_DOCUMENT = "MESSAGE_TYPE_DOCUMENT"
}
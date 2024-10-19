package com.joinflatshare.constants

import com.joinflatshare.pojo.user.User


object AppConstants {

    const val isAppLive = true

    const val isAdmin = false

    @JvmField
    var isSendbirdLive = false

    @JvmField
    var menuSelected = 0

    var hasNetworkConnection = true


    @JvmField
    var loggedInUser: User? = null

    @JvmField
    var isFeedReloadRequired = false

    // Languages and Interests
    const val interestMaxCount = 5
    const val languageMaxCount = 3

    // URLs
    const val urlTerms = "https://joinflatshare.com/terms-of-use"
    const val urlPrivacy = "https://joinflatshare.com/privacy-policy"


}
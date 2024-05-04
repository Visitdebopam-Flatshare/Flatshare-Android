package com.joinflatshare.constants

import com.joinflatshare.utils.helper.CommonMethod


object UrlConstants {
    var IMAGE_URL = ""
    var USER_IMAGE_URL = ""
    var AMENITY_URL = ""
    var INTEREST_URL = ""
    var LANGUAGE_URL = ""
    var VIDEO_URL = ""
    var ACTIVITY_URL = ""
    var PLANS_URL=""

    fun setAmazonImageUrl() {
        IMAGE_URL = AwsConstants.AMAZON_BASE_URL
        CommonMethod.makeLog("Amazon url", IMAGE_URL)
        USER_IMAGE_URL = "Images/"
        VIDEO_URL = IMAGE_URL + USER_IMAGE_URL
        AMENITY_URL = IMAGE_URL + "icons/amenity/"
        INTEREST_URL = IMAGE_URL + "icons/interests/"
        LANGUAGE_URL = IMAGE_URL + "icons/languages/"
        ACTIVITY_URL = IMAGE_URL + "icons/activities/"
        PLANS_URL = IMAGE_URL + "icons/plans/"
    }

    // Deep Linking
    const val DEEPLINK_BASE_URL = "https://joinflatshare.com/"
    const val DEEPLINK_FLATS_URL = "flats/"
    const val DEEPLINK_USERS_URL = "users/"
}

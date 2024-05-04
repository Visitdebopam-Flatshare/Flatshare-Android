package com.joinflatshare.constants

object ConfigConstants {

    val locationBypassNumbers = ArrayList<String>()

    // WEBSERVICE API ERROR CODES
    const val API_ERROR_CODE_WEBSERVICE = 400
    const val API_ERROR_CODE_BAD_REQUEST = 401
    const val API_ERROR_CODE_RESTRICT = 402
    const val API_ERROR_CODE_LOGOUT = 403
    const val API_ERROR_CODE_NOT_FOUND = 404

    const val COMPLETION_MINIMUM_FOR_USERS = 15
    const val COMPLETION_MINIMUM_FOR_FLATS = 50

    init {
        locationBypassNumbers.add("9832394089")
        locationBypassNumbers.add("6303546278")
        locationBypassNumbers.add("8169533929")
        locationBypassNumbers.add("0123456789")
        locationBypassNumbers.add("8652735449")
        locationBypassNumbers.add("9152991054")
        locationBypassNumbers.add("9004858787")
        locationBypassNumbers.add("9653325992")
        locationBypassNumbers.add("8291017414")
        locationBypassNumbers.add("8600888520")
        locationBypassNumbers.add("7391946849")
        locationBypassNumbers.add("7249783979")
        locationBypassNumbers.add("9326033851")
        locationBypassNumbers.add("9421804757")
        locationBypassNumbers.add("9836952801")
        locationBypassNumbers.add("8637326796")
    }
}
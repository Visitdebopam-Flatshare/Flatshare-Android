package com.joinflatshare.constants

/**
 * Created by debopam on 17/01/23
 */
object ReportConstants {
    val reportFlatMap = HashMap<String, Int>()
    val reportUserMap = HashMap<String, Int>()

    init {
        reportFlatMap["Inappropriate messages."] = 1
        reportFlatMap["Inappropriate photos."] = 2
        reportFlatMap["Seems like a fake flat."] = 3
        reportFlatMap["Seems like a broker."] = 4
        reportFlatMap["Asking for brokerage."] = 5

        reportUserMap["Inappropriate messages."] = 1
        reportUserMap["Inappropriate photos."] = 2
        reportUserMap["Seems like a fake profile."] = 3
        reportUserMap["Seems like a broker."] = 4
        reportUserMap["Asking for brokerage."] = 5
    }
}
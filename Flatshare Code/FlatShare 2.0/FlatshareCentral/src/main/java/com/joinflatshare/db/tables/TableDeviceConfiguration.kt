package com.joinflatshare.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TableDeviceConfiguration {
    @PrimaryKey(autoGenerate = false)
    var userId: String = ""

    var firstName: String = ""
    var lastName: String = ""
    var gender: String = ""
    var apiToken: String = ""

    var manufacturer: String = ""
    var brand: String = ""
    var model: String = ""
    var screenResolution: String = ""
    var osVersion: String = ""
    var sdkVersion: Int = 0
    var appVersionCode: Int = 0
    var appVersionName: String = ""
    var hasApprovedNotificationPermission: Boolean = true
}
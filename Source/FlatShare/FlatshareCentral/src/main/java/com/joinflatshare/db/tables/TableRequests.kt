package com.joinflatshare.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TableRequests(
    @PrimaryKey(autoGenerate = false)
    var notificationId: String,
    var jsonBody: String? = null,
    var requestType: String,
)

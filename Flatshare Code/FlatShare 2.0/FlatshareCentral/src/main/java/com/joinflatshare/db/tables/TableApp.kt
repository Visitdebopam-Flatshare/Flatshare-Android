package com.joinflatshare.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TableApp(
    @PrimaryKey(autoGenerate = false)
    var keyParam: String,
    var valueParam: String? = null,
)

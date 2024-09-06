package com.joinflatshare.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TableSendbirdUser(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var name: String? = null,
    var dp: String? = null,
)

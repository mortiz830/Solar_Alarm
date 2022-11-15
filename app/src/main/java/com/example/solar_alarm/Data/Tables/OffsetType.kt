package com.example.solar_alarm.Data.Tables

import androidx.room.Entity
import androidx.room.Index
import com.example.solar_alarm.Data.Tables.TableBase

@Entity(tableName = "OffsetTypes", indices = [Index(value = ["Name"], unique = true)])
class OffsetType : TableBase() {
    @JvmField
    var Name: String? = null
}
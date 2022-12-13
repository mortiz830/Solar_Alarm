package com.example.solar_alarm.Data.Tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.example.solar_alarm.Data.Tables.TableBase

@Entity(indices = [Index(value = ["Name"], unique = true)])
class SolarTimeType : TableBase() {
    //@JvmField
    //var Name: String? = null
    @ColumnInfo() val Name: String? = null
}
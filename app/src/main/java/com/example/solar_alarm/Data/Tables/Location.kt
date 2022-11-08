package com.example.solar_alarm.Data.Tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.solar_alarm.Data.Tables.TableBase
import com.example.solar_alarm.Data.Tables.Timezone

@Entity(tableName = "Location", indices = [Index(value = ["Name"], unique = true), Index(value = ["Latitude", "Longitude"], unique = true)])
class Location : TableBase() {
    @JvmField
    var Name: String = null

    @ForeignKey(entity = Timezone::class, parentColumns = ["Id"], childColumns = ["TimezoneId"])
    var TimezoneId = 0
    @JvmField
    var Latitude = 0.0
    @JvmField
    var Longitude = 0.0
}
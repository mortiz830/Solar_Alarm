package com.example.solar_alarm.Data.Tables

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "Location", indices = [Index(value = ["Name"], unique = true), Index(value = ["Latitude", "Longitude"], unique = true)])
class Location : TableBase() {
    @JvmField
    var Name: String? = null

    @JvmField
    var Latitude = 0.0
    @JvmField
    var Longitude = 0.0
}

//foreignKeys = [
//        ForeignKey(entity = Bar::class,
//                parentColumns = ["someCol"],
//                childColumns = ["someOtherCol"],
//                onDelete = CASCADE)])
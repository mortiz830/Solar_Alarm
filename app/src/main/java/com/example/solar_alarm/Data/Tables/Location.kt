package com.example.solar_alarm.Data.Tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
(
    tableName = "Location",
    indices =
    [
        Index(value = ["Name"],                  unique = true),
        Index(value = ["Latitude", "Longitude"], unique = true)
    ]
)

data class Location
(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") var Id:        Int = 0,
    @ColumnInfo(name = "Name")                                var Name:      String,
    @ColumnInfo(name = "Latitude")                            val Latitude:  Double,
    @ColumnInfo(name = "Longitude")                           val Longitude: Double
)
//{
//    companion object { const val TABLE_NAME = "Location" }
//}

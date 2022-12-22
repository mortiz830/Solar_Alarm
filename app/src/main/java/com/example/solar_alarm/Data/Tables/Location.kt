package com.example.solar_alarm.Data.Tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices =
    [
        Index(value = ["Name"], unique = true),
        Index(value = ["Latitude", "Longitude"], unique = true)
    ])

class Location
(
    @PrimaryKey(autoGenerate = true) val Id: Int,

    @ColumnInfo(name = "Name") val Name: String?,

    @ColumnInfo(name = "Latitude") val Latitude: Double,

    @ColumnInfo(name = "Longitude") val Longitude: Double
)

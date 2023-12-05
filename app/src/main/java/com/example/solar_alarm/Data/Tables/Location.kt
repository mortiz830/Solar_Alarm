package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
(
    tableName = "Location",
    indices =
    [
        Index(value = ["Name"],                  unique = true),
        Index(value = ["Latitude", "Longitude"]) //, unique = true - point not correct onclick
    ]
)

@RequiresApi(Build.VERSION_CODES.O)
data class Location
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                val Id        : Int,
    @ColumnInfo(name = "Name")              val Name      : String,
    @ColumnInfo(name = "Latitude")          val Latitude  : Double,
    @ColumnInfo(name = "Longitude")         val Longitude : Double,
    @ColumnInfo(name = "CreateDateTimeUtc") val CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
)


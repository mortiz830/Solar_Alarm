package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
(
    tableName = "Location",
    indices =
    [
        Index(value = ["Name"],                  unique = true),
        Index(value = ["Latitude", "Longitude"], unique = true)
    ]
)

@RequiresApi(Build.VERSION_CODES.O)
data class Location
(
    @ColumnInfo(name = "Name")      var Name      : String,
    @ColumnInfo(name = "Latitude")  var Latitude  : Double,
    @ColumnInfo(name = "Longitude") var Longitude : Double
)
{
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") var Id : Int = 0
    @ColumnInfo(name = "CreateDateTimeUtc") var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
}

package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
(
    tableName = "SolarTimeType",
    indices   = [Index(value = ["Name"], unique = true)]
)

@RequiresApi(Build.VERSION_CODES.O)
class SolarTimeType
(
    @PrimaryKey
    @ColumnInfo(name = "Id")   var Id   : Int,
    @ColumnInfo(name = "Name") var Name : String,
)
{
    @ColumnInfo(name = "CreateDateTimeUtc") var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
}

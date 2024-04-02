package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(
    tableName   = "SolarAlarm",
    indices     = [Index(value = ["Name", "LocationId"], unique = true)],
    foreignKeys =
    [
        ForeignKey
        (
            entity        = Location::class,
            parentColumns = ["Id"],
            childColumns  = ["LocationId"],
            onDelete      = ForeignKey.CASCADE
        ),

        ForeignKey
        (
            entity        = SolarTime::class,
            parentColumns = ["Id"],
            childColumns  = ["SolarTimeId"],
            onDelete      = ForeignKey.CASCADE
        ),

        ForeignKey
        (
            entity        = OffsetType::class,
            parentColumns = ["Id"],
            childColumns  = ["OffsetTypeId"],
            onDelete      = ForeignKey.CASCADE
        ),

        ForeignKey
        (
            entity        = SolarTimeType::class,
            parentColumns = ["Id"],
            childColumns  = ["SolarTimeTypeId"],
            onDelete      = ForeignKey.CASCADE
        )
    ]
)

data class SolarAlarm
(
    @ColumnInfo(name = "Active") var Active : Boolean,

    @ColumnInfo(name = "Name")   var Name : String,

    @ColumnInfo(name = "LocationId")  var LocationId  : Int,
    @ColumnInfo(name = "SolarTimeId") var SolarTimeId : Int,

    // Recurrence Flags
    @ColumnInfo(name = "Recurring") var Recurring: Boolean,
    @ColumnInfo(name = "Monday")    var Monday:    Boolean,
    @ColumnInfo(name = "Tuesday")   var Tuesday:   Boolean,
    @ColumnInfo(name = "Wednesday") var Wednesday: Boolean,
    @ColumnInfo(name = "Thursday")  var Thursday:  Boolean,
    @ColumnInfo(name = "Friday")    var Friday:    Boolean,
    @ColumnInfo(name = "Saturday")  var Saturday:  Boolean,
    @ColumnInfo(name = "Sunday")    var Sunday:    Boolean,

    @ColumnInfo(name = "OffsetTypeId")    var OffsetTypeId:    OffsetTypeEnum,
    @ColumnInfo(name = "SolarTimeTypeId") var SolarTimeTypeId: SolarTimeTypeEnum
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                var Id                : Int = 0
    @ColumnInfo(name = "CreateDateTimeUtc") var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
}
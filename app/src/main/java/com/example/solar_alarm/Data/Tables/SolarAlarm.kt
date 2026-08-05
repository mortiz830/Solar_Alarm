package com.example.solar_alarm.data.tables

// Repair: Fixed broken package/import lines
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(
    tableName   = "SolarAlarm",
    indices     =
    [
        Index(value = ["Name", "LocationId"], unique = true,  name = "UniqueLocationName"),
        Index(value = ["LocationId"],         unique = false, name = "SolarAlarmLocationIndex"),
        Index(value = ["SolarTimeId"],        unique = false, name = "SolarTimeIndex")
    ],
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
        )
    ]
)

@Parcelize
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
) : Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                var Id                : Int = 0
    @ColumnInfo(name = "CreateDateTimeUtc") var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)

    init
    {
        require(Name.isNotBlank()) { "SolarAlarm name cannot be empty or consist only of whitespace." }
    }
}

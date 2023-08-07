package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(
    tableName = "SolarAlarm",
    indices = [Index(value = ["Name", "LocationId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["Id"],
            childColumns = ["LocationId"],
            onDelete = ForeignKey.CASCADE),

        ForeignKey(
            entity = SolarTime::class,
            parentColumns = ["Id"],
            childColumns = ["SolarTimeId"],
            onDelete = ForeignKey.CASCADE),

        ForeignKey(
            entity = OffsetType::class,
            parentColumns = ["Id"],
            childColumns = ["OffsetTypeId"],
            onDelete = ForeignKey.CASCADE),

        ForeignKey(
            entity = SolarTimeType::class,
            parentColumns = ["Id"],
            childColumns = ["SolarTimeTypeId"],
            onDelete = ForeignKey.CASCADE)
    ]
)

class SolarAlarm
(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id")  var Id:          Int = 0,
    @ColumnInfo(name = "Name")        var Name:        String,
    @ColumnInfo(name = "Active")      var Active:      Boolean,
    @ColumnInfo(name = "LocationId")  var LocationId:  Int,
    @ColumnInfo(name = "SolarTimeId") var SolarTimeId: Int,

    // Recurrence Flags
    @ColumnInfo(name = "Recurring") var Recurring: Boolean,
    @ColumnInfo(name = "Monday")    var Monday:    Boolean,
    @ColumnInfo(name = "Tuesday")   var Tuesday:   Boolean,
    @ColumnInfo(name = "Wednesday") var Wednesday: Boolean,
    @ColumnInfo(name = "Thursday")  var Thursday:  Boolean,
    @ColumnInfo(name = "Friday")    var Friday:    Boolean,
    @ColumnInfo(name = "Saturday")  var Saturday:  Boolean,
    @ColumnInfo(name = "Sunday")    var Sunday:    Boolean,

    @ColumnInfo(name = "OffsetTypeId") var OffsetTypeId: OffsetTypeEnum,

    @ColumnInfo(name = "SolarTimeTypeId") var SolarTimeTypeId: SolarTimeTypeEnum,
)
{
    companion object { const val TABLE_NAME = "SolarAlarm" }

    val recurringDaysText: String?
        get() {
            if (!Recurring)
            {
                return null
            }

            var days = ""

            if (Monday) {
                days += "Mo "
            }
            if (Tuesday) {
                days += "Tu "
            }
            if (Wednesday) {
                days += "We "
            }
            if (Thursday) {
                days += "Th "
            }
            if (Friday) {
                days += "Fr "
            }
            if (Saturday) {
                days += "Sa "
            }
            if (Sunday) {
                days += "Su "
            }
            return days
        }
}
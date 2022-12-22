package com.example.solar_alarm.Data.Tables

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.room.*
import com.example.solar_alarm.Data.Tables.TableBase
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Tables.SolarTimeType
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(indices = [Index(value = ["Name", "LocationId"], unique = true)], foreignKeys = [
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
        onDelete = ForeignKey.CASCADE)]
)
class SolarAlarm (

    @PrimaryKey(autoGenerate = true) val Id: Int,

    @ColumnInfo(name = "Name") val Name: String?,

    @ColumnInfo(name = "Active") val Active: Boolean,

    @ColumnInfo(name = "LocationId") val LocationId: Int,

    @ColumnInfo(name = "SolarTimeId") val SolarTimeId: Int,

    // Recurrence Flags
    @ColumnInfo(name = "Recurring") val Recurring: Boolean,
    @ColumnInfo(name = "Monday")    val Monday: Boolean,
    @ColumnInfo(name = "Tuesday")   val Tuesday: Boolean,
    @ColumnInfo(name = "Wednesday") val Wednesday: Boolean,
    @ColumnInfo(name = "Thursday")  val Thursday: Boolean,
    @ColumnInfo(name = "Friday")    val Friday: Boolean,
    @ColumnInfo(name = "Saturday")  val Saturday: Boolean,
    @ColumnInfo(name = "Sunday")    val Sunday: Boolean,

    @ColumnInfo(name = "OffsetTypeId") val OffsetTypeId: OffsetTypeEnum,

    @ColumnInfo(name = "SolarTimeTypeId") val SolarTimeTypeId: SolarTimeTypeEnum,
)
{
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
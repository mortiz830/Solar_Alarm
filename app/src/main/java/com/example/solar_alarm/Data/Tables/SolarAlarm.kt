package com.example.solar_alarm.Data.Tables

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.solar_alarm.Data.Tables.TableBase
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Tables.SolarTimeType
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(tableName = "SolarAlarm", indices = [Index(value = ["Name", "LocationId"], unique = true)])
class SolarAlarm : TableBase() {
    @JvmField
    var Name: String = null
    @JvmField
    var Active = false

    @JvmField
    @ForeignKey(entity = Location::class, parentColumns = ["Id"], childColumns = ["LocationId"])
    var LocationId = 0

    @JvmField
    @ForeignKey(entity = SolarTime::class, parentColumns = ["Id"], childColumns = ["SolarTimeId"])
    var SolarTimeId = 0

    // Recurrence Flags
    @JvmField
    var Recurring = false
    @JvmField
    var Monday = false
    @JvmField
    var Tuesday = false
    @JvmField
    var Wednesday = false
    @JvmField
    var Thursday = false
    @JvmField
    var Friday = false
    @JvmField
    var Saturday = false
    @JvmField
    var Sunday = false

    @JvmField
    @ForeignKey(entity = OffsetType::class, parentColumns = ["Id"], childColumns = ["OffsetTypeId"])
    var OffsetTypeId: OffsetTypeEnum? = null

    @JvmField
    @ForeignKey(entity = SolarTimeType::class, parentColumns = ["Id"], childColumns = ["SolarTimeTypeId"])
    var SolarTimeTypeId: SolarTimeTypeEnum? = null
    val recurringDaysText: String?
        get() {
            if (!Recurring) {
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
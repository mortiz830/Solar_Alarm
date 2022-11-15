package com.example.solar_alarm.Data

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import androidx.room.*
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(api = Build.VERSION_CODES.O)
object Converters {
    private val dateFormat = DateTimeFormatter.ISO_DATE
    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return if (dateString == null) null else LocalDate.parse(dateString)
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.format(dateFormat)
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toOffsetTypeId(enumType: OffsetTypeEnum): Int {
        return enumType.Id
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toOffsetTypeEnum(id: Int): OffsetTypeEnum {
        return OffsetTypeEnum.values()[id]
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toSolarTimeTypeId(enumType: SolarTimeTypeEnum): Int {
        return enumType.Id
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toSolarTimeTypeEnum(id: Int): SolarTimeTypeEnum {
        return SolarTimeTypeEnum.values()[id]
    }

    @TypeConverter
    fun toTimeString(zonedDateTime: ZonedDateTime): Array<String> {
        // We will need to consider giving the user to choose their date and time formats.
        val hour: String
        val hourInt = if (zonedDateTime.hour > 12) zonedDateTime.hour - 12 else zonedDateTime.hour
        hour = if (hourInt < 10) {
            String.format("%02d", hourInt)
        } else {
            hourInt.toString()
        }
        val ampm = if (zonedDateTime.hour > 11) "PM" else "AM"
        val time = hour + ":" + zonedDateTime.minute + " " + ampm

        //String dayOfWeek  = zonedDateTime.getDayOfWeek().toString().substring(0,3);
        val dayOfMonth = if (zonedDateTime.dayOfMonth < 10) String.format("%02d", zonedDateTime.dayOfMonth) else zonedDateTime.dayOfMonth.toString()
        val month = zonedDateTime.month.toString().substring(0, 3)
        val date =  /*dayOfWeek + " " +*/dayOfMonth + "-" + month + "-" + zonedDateTime.year
        return arrayOf(date, time)
    }
}
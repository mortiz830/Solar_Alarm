package com.example.solar_alarm.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(api = Build.VERSION_CODES.O)
object Converters
{
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }

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
        return OffsetTypeEnum.entries.find { it.Id == id } ?: OffsetTypeEnum.At
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toSolarTimeTypeId(enumType: SolarTimeTypeEnum): Int {
        return enumType.Id
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun toSolarTimeTypeEnum(id: Int): SolarTimeTypeEnum {
        return SolarTimeTypeEnum.entries.find { it.Id == id } ?: SolarTimeTypeEnum.Sunrise
    }

    @TypeConverter
    fun toTimeString(zonedDateTime: ZonedDateTime): Array<String> {
        // We will need to consider giving the user to choose their date and time formats.
        val hour12 = when {
            zonedDateTime.hour == 0 -> 12
            zonedDateTime.hour > 12 -> zonedDateTime.hour - 12
            else -> zonedDateTime.hour
        }
        val ampm = if (zonedDateTime.hour >= 12) "PM" else "AM"
        val time = String.format(java.util.Locale.getDefault(), "%02d:%02d %s", hour12, zonedDateTime.minute, ampm)

        val dayOfMonth = String.format(java.util.Locale.getDefault(), "%02d", zonedDateTime.dayOfMonth)
        val month = zonedDateTime.month.toString().substring(0, 3)
        val date =  dayOfMonth + "-" + month + "-" + zonedDateTime.year
        return arrayOf(date, time)
    }
}
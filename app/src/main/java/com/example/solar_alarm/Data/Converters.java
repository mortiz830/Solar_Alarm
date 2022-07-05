package com.example.solar_alarm.Data;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Enums.TimeUnitTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Converters
{
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_DATE_TIME;
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;
    private static DateTimeFormatter timeFormat = DateTimeFormatter.ISO_TIME;

    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime localDateTime)
    {
        return localDateTime == null ? null : localDateTime.format(dateTimeFormat);
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String dateTimeString)
    {
        return dateTimeString == null ? null : LocalDateTime.parse(dateTimeString, dateTimeFormat);
    }

    @TypeConverter
    public static LocalDate toLocalDate(String dateString)
    {
        return dateString == null ? null: LocalDate.parse(dateString);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate localDate)
    {
        return localDate == null ? null : localDate.format(dateFormat);
    }

    @TypeConverter
    public static LocalTime toLocalTime(String timeString)
    {
        return timeString == null ? null: LocalTime.parse(timeString);
    }

    @TypeConverter
    public static String fromLocalTime(LocalTime localTime)
    {
        return localTime == null ? null : localTime.format(timeFormat);
    }

    @TypeConverter
    public static int toOffsetTypeId(OffsetTypeEnum enumType) { return enumType.Id; }

    @TypeConverter
    public static OffsetTypeEnum toOffsetTypeEnum(int id) { return OffsetTypeEnum.values()[id]; }

    @TypeConverter
    public static int toSolarTimeTypeId(SolarTimeTypeEnum enumType) { return enumType.Id; }

    @TypeConverter
    public static SolarTimeTypeEnum toSolarTimeTypeEnum(int id) { return SolarTimeTypeEnum.values()[id]; }

    @TypeConverter
    public static int toTimeUnitTypeId(TimeUnitTypeEnum enumType) { return enumType.Id; }

    @TypeConverter
    public static TimeUnitTypeEnum toTimeUnitTypeEnum(int id) { return TimeUnitTypeEnum.values()[id]; }
}

package com.example.solar_alarm.Data;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Converters
{
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;

    @TypeConverter
    public static LocalDate toLocalDate(String dateString) { return dateString == null ? null: LocalDate.parse(dateString); }

    @TypeConverter
    public static String fromLocalDate(LocalDate localDate) { return localDate == null ? null : localDate.format(dateFormat); }

    @TypeConverter
    public static int toOffsetTypeId(OffsetTypeEnum enumType) { return enumType.Id; }

    @TypeConverter
    public static OffsetTypeEnum toOffsetTypeEnum(int id) { return OffsetTypeEnum.values()[id]; }

    @TypeConverter
    public static int toSolarTimeTypeId(SolarTimeTypeEnum enumType) { return enumType.Id; }

    @TypeConverter
    public static SolarTimeTypeEnum toSolarTimeTypeEnum(int id) { return SolarTimeTypeEnum.values()[id]; }
}

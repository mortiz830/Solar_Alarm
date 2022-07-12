package com.example.solar_alarm.Data;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;

import java.time.LocalDate;
import java.time.ZonedDateTime;
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

    @TypeConverter
    public static String[] toTimeString(ZonedDateTime zonedDateTime)
    {
        // We will need to consider giving the user to choose their date and time formats.
        String hour;
        int hourInt = zonedDateTime.getHour() > 12 ? zonedDateTime.getHour() - 12 : zonedDateTime.getHour();

        if (hourInt < 10)
        {
            hour = String.format("%02d" , hourInt);
        }
        else
        {
            hour = String.valueOf(hourInt);
        }

        String ampm       = zonedDateTime.getHour() > 11 ? "PM" : "AM";
        String time       = hour + ":" + zonedDateTime.getMinute() + " " + ampm;

        //String dayOfWeek  = zonedDateTime.getDayOfWeek().toString().substring(0,3);
        String dayOfMonth = zonedDateTime.getDayOfMonth() < 10 ? String.format("%02d" , zonedDateTime.getDayOfMonth()) : String.valueOf(zonedDateTime.getDayOfMonth());
        String month      = zonedDateTime.getMonth().toString().substring(0,3);
        String date       = /*dayOfWeek + " " +*/ dayOfMonth + "-" + month + "-" + zonedDateTime.getYear();

        return new String[] {date, time};
    }
}

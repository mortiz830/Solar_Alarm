package dataAccess;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

public class Converters {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static LocalTime fromTimestamp(Long value)
    {
        return (value == null) ? null : LocalTime.ofSecondOfDay(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static Long dateToTimestamp(LocalTime date)
    {
        return date == null ? null : date.getLong(ChronoField.M);
    }
}
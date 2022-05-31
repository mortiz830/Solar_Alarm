package com.example.solar_alarm.Data.Tables;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity
(
    tableName = "SolarTime",
    indices = {@Index(value = {"Date", "LocationId"}, unique = true)}
)

public class SolarTime extends TableBase
{
    @NonNull
    public LocalDate Date;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;
    public int DayLength;

    // UTC Time Stings in ISO_OFFSET_DATE_TIME format
    public String SunriseUtc;
    public String SunsetUtc;
    public String SolarNoonUtc;
    public String CivilTwilightBeginUtc;
    public String CivilTwilightEndUtc;
    public String NauticalTwilightBeginUtc;
    public String NauticalTwilightEndUtc;
    public String AstronomicalTwilightBeginUtc;
    public String AstronomicalTwilightEndUtc;

    public SolarTime (){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime (Location location, SunriseSunsetResponse sunriseSunsetResponse)
    {
        Date                         = LocalDateTime.ofInstant(sunriseSunsetResponse.request.Date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocationId                   = location.Id;
        DayLength                    = sunriseSunsetResponse.getDayLength();
        SunriseUtc                   = sunriseSunsetResponse.getSunrise();
        SunsetUtc                    = sunriseSunsetResponse.getSunset();
        SolarNoonUtc                 = sunriseSunsetResponse.getSolarNoon();
        CivilTwilightBeginUtc        = sunriseSunsetResponse.getCivilTwilightBegin();
        CivilTwilightEndUtc          = sunriseSunsetResponse.getCivilTwilightEnd();
        NauticalTwilightBeginUtc     = sunriseSunsetResponse.getNauticalTwilightBegin();
        NauticalTwilightEndUtc       = sunriseSunsetResponse.getNauticalTwilightEnd();
        AstronomicalTwilightBeginUtc = sunriseSunsetResponse.getAstronomicalTwilightBegin();
        AstronomicalTwilightEndUtc   = sunriseSunsetResponse.getAstronomicalTwilightEnd();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime GetLocalDateTime(SolarTimeTypeEnum solarTimeTypeEnum) throws Exception
    {
        ZonedDateTime utcDateTime   = GetUtcDateTime(solarTimeTypeEnum);
        ZoneId        zoneId        = ZoneId.systemDefault();
        ZonedDateTime LocalDateTime = utcDateTime.withZoneSameInstant(zoneId);

        return LocalDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ZonedDateTime GetUtcDateTime(SolarTimeTypeEnum solarTimeTypeEnum) throws Exception
    {
        LocalDateTime localDateTime = getLocalDateTime(solarTimeTypeEnum);
        ZoneId        zoneId        = ZoneId.ofOffset("UTC", ZoneOffset.UTC);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        return zonedDateTime;
    }

    private LocalDateTime getLocalDateTime(SolarTimeTypeEnum solarTimeTypeEnum) throws Exception
    {
        final DateTimeFormatter _DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocalDateTime localDateTime;

        switch (solarTimeTypeEnum)
        {
            case Sunrise:
                localDateTime = LocalDateTime.parse(SunriseUtc, _DateTimeFormatter);
                break;
            case Sunset:
                localDateTime = LocalDateTime.parse(SunsetUtc, _DateTimeFormatter);
                break;
            case SolarNoon:
                localDateTime = LocalDateTime.parse(SolarNoonUtc, _DateTimeFormatter);
                break;
            case CivilTwilightBegin:
                localDateTime = LocalDateTime.parse(CivilTwilightBeginUtc, _DateTimeFormatter);
                break;
            case CivilTwilightEnd:
                localDateTime = LocalDateTime.parse(CivilTwilightEndUtc, _DateTimeFormatter);
                break;
            case NauticalTwilightBegin:
                localDateTime = LocalDateTime.parse(NauticalTwilightBeginUtc, _DateTimeFormatter);
                break;
            case NauticalTwilightEnd:
                localDateTime = LocalDateTime.parse(NauticalTwilightEndUtc, _DateTimeFormatter);
                break;
            case AstronomicalTwilightBegin:
                localDateTime = LocalDateTime.parse(AstronomicalTwilightBeginUtc, _DateTimeFormatter);
                break;
            case AstronomicalTwilightEnd:
                localDateTime = LocalDateTime.parse(AstronomicalTwilightEndUtc, _DateTimeFormatter);
                break;
            default:
                throw new Exception("Enum not implemented");
        }

        return localDateTime;
    }
}

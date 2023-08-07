package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(
        tableName = "SolarTime",
        indices = [Index(value = ["SolarDate", "LocationId"], unique = true)],
        foreignKeys =
        [
            ForeignKey(
                entity = Location::class,
                parentColumns = ["Id"],
                childColumns = ["LocationId"],
                onDelete = ForeignKey.CASCADE)
        ]
)
class SolarTime
(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id")                   var Id: Int = 0,
    @ColumnInfo(name = "SolarDate")                    val SolarDate: LocalDate,
    @ColumnInfo(name = "LocationId")                   val LocationId: Int,
    @ColumnInfo(name = "DayLength")                    val DayLength: Int,
    @ColumnInfo(name = "SunriseUtc")                   val SunriseUtc: String?,
    @ColumnInfo(name = "SunsetUtc")                    val SunsetUtc: String?,
    @ColumnInfo(name = "SolarNoonUtc")                 val SolarNoonUtc: String?,
    @ColumnInfo(name = "CivilTwilightBeginUtc")        val CivilTwilightBeginUtc: String?,
    @ColumnInfo(name = "CivilTwilightEndUtc")          val CivilTwilightEndUtc: String?,
    @ColumnInfo(name = "NauticalTwilightBeginUtc")     val NauticalTwilightBeginUtc: String?,
    @ColumnInfo(name = "NauticalTwilightEndUtc")       val NauticalTwilightEndUtc: String?,
    @ColumnInfo(name = "AstronomicalTwilightBeginUtc") val AstronomicalTwilightBeginUtc: String?,
    @ColumnInfo(name = "AstronomicalTwilightEndUtc")   val AstronomicalTwilightEndUtc: String?
)
{
    companion object { const val TABLE_NAME = "SolarTime" }

    fun GetLocalZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime
    {
        val utcDateTime = GetUtcZonedDateTime(solarTimeTypeEnum)
        val zoneId      = ZoneId.systemDefault()

        return utcDateTime.withZoneSameInstant(zoneId)
    }

    private fun GetUtcZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime
    {
        val localDateTime = getLocalDateTime(solarTimeTypeEnum)
        val zoneId        = ZoneId.ofOffset("UTC", ZoneOffset.UTC)

        return localDateTime.atZone(zoneId)
    }

    private fun getLocalDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): LocalDateTime
    {
        val utcString: String? = when (solarTimeTypeEnum)
        {
            SolarTimeTypeEnum.Sunrise                   -> SunriseUtc
            SolarTimeTypeEnum.Sunset                    -> SunsetUtc
            SolarTimeTypeEnum.SolarNoon                 -> SolarNoonUtc
            SolarTimeTypeEnum.CivilTwilightBegin        -> CivilTwilightBeginUtc
            SolarTimeTypeEnum.CivilTwilightEnd          -> CivilTwilightEndUtc
            SolarTimeTypeEnum.NauticalTwilightBegin     -> NauticalTwilightBeginUtc
            SolarTimeTypeEnum.NauticalTwilightEnd       -> NauticalTwilightEndUtc
            SolarTimeTypeEnum.AstronomicalTwilightBegin -> AstronomicalTwilightBeginUtc
            SolarTimeTypeEnum.AstronomicalTwilightEnd   -> AstronomicalTwilightEndUtc
        }

        return LocalDateTime.parse(utcString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}

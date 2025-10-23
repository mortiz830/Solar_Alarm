package com.example.solar_alarm.Data.Tables

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity
(
        tableName   = "SolarTime",
        indices     =
        [
            Index(value = ["SolarDate", "LocationId"], unique = true,  name = "UniqueDateAndLocation"),
            Index(value = ["LocationId"],              unique = false, name = "SolarTimeLocationIndex")
        ],
        foreignKeys =
        [
            ForeignKey
            (
                entity        = Location::class,
                parentColumns = ["Id"],
                childColumns  = ["LocationId"],
                onDelete      = ForeignKey.CASCADE
            )
        ]
)

@Parcelize
data class SolarTime
(
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
) : Parcelable
{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") var Id : Int = 0
    @IgnoredOnParcel
    @ColumnInfo(name = "CreateDateTimeUtc") var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)

    /**
     * @param solarTimeTypeEnum the type of time.
     * @return UTC date time string in UTC time zone as a ZonedDateTime
     * @see ZonedDateTime
     */
    fun GetLocalZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime
    {
        val utcDateTime = GetUtcZonedDateTime(solarTimeTypeEnum)
        val zoneId      = ZoneId.systemDefault()

        return utcDateTime.withZoneSameInstant(zoneId)
    }

    /**
     * @param solarTimeTypeEnum the type of time.
     * @return UTC date time string in UTC time zone as a ZonedDateTime
     * @see ZonedDateTime
     */
    private fun GetUtcZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime
    {
        val localDateTime = getLocalDateTime(solarTimeTypeEnum)
        val zoneId        = ZoneId.ofOffset("UTC", ZoneOffset.UTC)

        return localDateTime.atZone(zoneId)
    }

    /**
     * @param solarTimeTypeEnum the type of time.
     * @return UTC date time string converted to the device's time zone as a LocalDateTime
     * @see LocalDateTime
     */
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

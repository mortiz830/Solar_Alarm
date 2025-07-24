package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.solar_alarm.SolarAlarmApp
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
(
    tableName = "Location",
    indices =
    [
        Index(value = ["Name"],                  unique = true),
        Index(value = ["Latitude", "Longitude"], unique = true)
    ]
)

@RequiresApi(Build.VERSION_CODES.O)
data class Location
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                val Id        : Int,
    @ColumnInfo(name = "Name")              val Name      : String,
    @ColumnInfo(name = "Latitude")          val Latitude  : Double,
    @ColumnInfo(name = "Longitude")         val Longitude : Double,
    @ColumnInfo(name = "CreateDateTimeUtc") val CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
)
{
    private var _solarTimes : ArrayList<SolarTime> = arrayListOf()

    val solarTimes : ArrayList<SolarTime>
        get() {
            if (_solarTimes.isEmpty())
            {
                _solarTimes = GetSolarTimes()
            }

            return _solarTimes
        }

    private fun GetSolarTimes() : ArrayList<SolarTime>
    {
        val solarTimes : ArrayList<SolarTime> = arrayListOf()
        var date                              = LocalDate.now()
        val thisLocation                      = this

        for (i in 1..7)
        {
            try
            {
                runBlocking {
                    val solarTime = SolarAlarmApp().solarTimeRepository.getSolarTime(thisLocation, date)

                    if (solarTime != null)
                    {
                        solarTimes.add(solarTime)
                    }
                }

                date = date.plusDays(1)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                throw e
            }
        }

        return solarTimes
    }

    fun Refresh()
    {
        _solarTimes.clear()
    }
}

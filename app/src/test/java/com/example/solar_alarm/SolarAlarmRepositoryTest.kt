package com.example.solar_alarm

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import com.example.solar_alarm.data.repositories.SolarAlarmRepository
import com.example.solar_alarm.data.repositories.SolarAlarmRepositoryImpl
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.LocalDate

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class SolarAlarmRepositoryTest {

    private lateinit var database: SolarAlarmDatabase
    private lateinit var locationDao: LocationDao
    private lateinit var solarTimeDao: SolarTimeDao
    private lateinit var solarAlarmDao: SolarAlarmDao
    private lateinit var repository: SolarAlarmRepository

    private val testLocation = Location(
        Id = 1,
        Name = "Tokyo",
        Latitude = 35.6762,
        Longitude = 139.6503
    )

    private val testDate = LocalDate.of(2026, 10, 26)

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SolarAlarmDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        locationDao = database.locationDao()
        solarTimeDao = database.solarTimeDao()
        solarAlarmDao = database.solarAlarmDao()

        locationDao.insert(testLocation)

        repository = SolarAlarmRepositoryImpl(database, solarAlarmDao, solarTimeDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun insertSampleSolarTime(id: Int = 1): SolarTime {
        val solarTime = SolarTime(
            SolarDate = testDate,
            LocationId = testLocation.Id,
            DayLength = 40000,
            SunriseUtc = "2026-10-26T06:00:00Z",
            SunsetUtc = "2026-10-26T18:00:00Z",
            SolarNoonUtc = "2026-10-26T12:00:00Z",
            CivilTwilightBeginUtc = null,
            CivilTwilightEndUtc = null,
            NauticalTwilightBeginUtc = null,
            NauticalTwilightEndUtc = null,
            AstronomicalTwilightBeginUtc = null,
            AstronomicalTwilightEndUtc = null
        ).apply { Id = id }
        solarTimeDao.insert(solarTime)
        return solarTimeDao.getById(id)
    }

    @Test
    fun testInsertAndGetById() = runBlocking {
        val solarTime = insertSampleSolarTime(1)

        val alarm = SolarAlarm(
            Active = true,
            Name = "Sunrise Wakeup",
            LocationId = testLocation.Id,
            SolarTimeId = solarTime.Id,
            Recurring = true,
            Monday = true, Tuesday = true, Wednesday = true, Thursday = true, Friday = true,
            Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.Before,
            SolarTimeTypeId = SolarTimeTypeEnum.Sunrise,
            OffsetHours = 0,
            OffsetMinutes = 15
        )
        repository.insert(alarm)

        val allAlarms = repository.all.first()
        assertEquals(1, allAlarms.size)

        val fetched = repository.getById(allAlarms[0].Id)
        assertNotNull(fetched)
        assertEquals("Sunrise Wakeup", fetched?.Name)
        assertEquals(15, fetched?.OffsetMinutes)
    }

    @Test
    fun testTransactionalDelete_removesUnusedSolarTime() = runBlocking {
        val solarTime = insertSampleSolarTime(1)

        val alarm = SolarAlarm(
            Active = true,
            Name = "Single Alarm",
            LocationId = testLocation.Id,
            SolarTimeId = solarTime.Id,
            Recurring = false,
            Monday = false, Tuesday = false, Wednesday = false, Thursday = false, Friday = false,
            Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.At,
            SolarTimeTypeId = SolarTimeTypeEnum.Sunrise,
            OffsetHours = 0,
            OffsetMinutes = 0
        )
        repository.insert(alarm)

        val insertedAlarm = repository.all.first()[0]
        assertNotNull(solarTimeDao.getById(solarTime.Id))

        // Delete should transactionally delete both the alarm and the now-unused solar time
        repository.delete(insertedAlarm)

        assertNull(repository.getById(insertedAlarm.Id))
        assertNull(solarTimeDao.getSolarTime(testLocation.Id, testDate))
    }

    @Test
    fun testTransactionalDelete_preservesSharedSolarTime() = runBlocking {
        val solarTime = insertSampleSolarTime(1)

        val alarm1 = SolarAlarm(
            Active = true,
            Name = "Alarm 1",
            LocationId = testLocation.Id,
            SolarTimeId = solarTime.Id,
            Recurring = false,
            Monday = false, Tuesday = false, Wednesday = false, Thursday = false, Friday = false,
            Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.At,
            SolarTimeTypeId = SolarTimeTypeEnum.Sunrise,
            OffsetHours = 0,
            OffsetMinutes = 0
        )
        val alarm2 = SolarAlarm(
            Active = true,
            Name = "Alarm 2",
            LocationId = testLocation.Id,
            SolarTimeId = solarTime.Id,
            Recurring = false,
            Monday = false, Tuesday = false, Wednesday = false, Thursday = false, Friday = false,
            Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.Before,
            SolarTimeTypeId = SolarTimeTypeEnum.Sunrise,
            OffsetHours = 0,
            OffsetMinutes = 30
        )
        repository.insert(alarm1)
        repository.insert(alarm2)

        val allAlarms = repository.all.first()
        assertEquals(2, allAlarms.size)

        val firstAlarm = allAlarms.first { it.Name == "Alarm 1" }

        // Deleting Alarm 1 must NOT delete the shared SolarTime because Alarm 2 still references it
        repository.delete(firstAlarm)

        assertEquals(1, repository.all.first().size)
        assertNull(repository.getById(firstAlarm.Id))
        assertNotNull(solarTimeDao.getById(solarTime.Id))
    }
}

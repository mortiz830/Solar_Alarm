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
class DatabaseCrudTest {

    private lateinit var database: SolarAlarmDatabase
    private lateinit var locationDao: LocationDao
    private lateinit var solarTimeDao: SolarTimeDao
    private lateinit var solarAlarmDao: SolarAlarmDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SolarAlarmDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        locationDao = database.locationDao()
        solarTimeDao = database.solarTimeDao()
        solarAlarmDao = database.solarAlarmDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testLocationCrud() = runBlocking {
        // --- Create ---
        val location = Location(
            Id = 0,
            Name = "San Francisco",
            Latitude = 37.7749,
            Longitude = -122.4194
        )
        locationDao.insert(location)

        val insertedLocation = locationDao.getByName("San Francisco")
        assertNotNull(insertedLocation)
        assertEquals("San Francisco", insertedLocation.Name)
        assertEquals(37.7749, insertedLocation.Latitude, 0.0001)

        // --- Read ---
        assertTrue(locationDao.doesLocationNameExists("San Francisco"))
        assertTrue(locationDao.doesLocationLatLongExists(37.7749, -122.4194))

        val allLocations = locationDao.getAll().first()
        assertEquals(1, allLocations.size)

        // --- Update ---
        val updatedLocation = Location(
            Id = insertedLocation.Id,
            Name = "SF Bay Area",
            Latitude = 37.7749,
            Longitude = -122.4194
        )
        val updateCount = locationDao.update(updatedLocation)
        assertEquals(1, updateCount)

        val retrievedUpdated = locationDao.getById(insertedLocation.Id)
        assertEquals("SF Bay Area", retrievedUpdated.Name)

        // --- Delete ---
        locationDao.delete(retrievedUpdated)
        assertFalse(locationDao.doesLocationNameExists("SF Bay Area"))
    }

    @Test
    fun testSolarTimeCrud() = runBlocking {
        // Setup Location
        val location = Location(Id = 0, Name = "NYC", Latitude = 40.7128, Longitude = -74.0060)
        locationDao.insert(location)
        val insertedLocation = locationDao.getByName("NYC")

        val today = LocalDate.now()

        // --- Create ---
        val solarTime = SolarTime(
            SolarDate = today,
            LocationId = insertedLocation.Id,
            DayLength = 43200,
            SunriseUtc = "2025-01-01T11:00:00Z",
            SunsetUtc = "2025-01-01T23:00:00Z",
            SolarNoonUtc = "2025-01-01T17:00:00Z",
            CivilTwilightBeginUtc = "2025-01-01T10:30:00Z",
            CivilTwilightEndUtc = "2025-01-01T23:30:00Z",
            NauticalTwilightBeginUtc = null,
            NauticalTwilightEndUtc = null,
            AstronomicalTwilightBeginUtc = null,
            AstronomicalTwilightEndUtc = null
        )
        solarTimeDao.insert(solarTime)

        // --- Read ---
        assertTrue(solarTimeDao.doesLocationIdDatePairExists(insertedLocation.Id, today))
        val retrievedTime = solarTimeDao.getSolarTime(insertedLocation.Id, today)
        assertNotNull(retrievedTime)
        assertEquals("2025-01-01T11:00:00Z", retrievedTime?.SunriseUtc)

        // --- Update ---
        val updatedTime = SolarTime(
            SolarDate = today,
            LocationId = insertedLocation.Id,
            DayLength = 44000,
            SunriseUtc = "2025-01-01T11:05:00Z",
            SunsetUtc = "2025-01-01T23:05:00Z",
            SolarNoonUtc = "2025-01-01T17:05:00Z",
            CivilTwilightBeginUtc = "2025-01-01T10:30:00Z",
            CivilTwilightEndUtc = "2025-01-01T23:30:00Z",
            NauticalTwilightBeginUtc = null,
            NauticalTwilightEndUtc = null,
            AstronomicalTwilightBeginUtc = null,
            AstronomicalTwilightEndUtc = null
        ).apply { Id = retrievedTime!!.Id }

        val updateCount = solarTimeDao.update(updatedTime)
        assertEquals(1, updateCount)

        val reFetched = solarTimeDao.getById(retrievedTime!!.Id)
        assertEquals("2025-01-01T11:05:00Z", reFetched.SunriseUtc)

        // --- Delete ---
        solarTimeDao.delete(reFetched)
        assertFalse(solarTimeDao.doesLocationIdDatePairExists(insertedLocation.Id, today))
    }

    @Test
    fun testSolarAlarmCrud() = runBlocking {
        // Setup Location & SolarTime
        locationDao.insert(Location(Id = 0, Name = "Tokyo", Latitude = 35.6762, Longitude = 139.6503))
        val location = locationDao.getByName("Tokyo")

        val today = LocalDate.now()
        solarTimeDao.insert(SolarTime(
            SolarDate = today,
            LocationId = location.Id,
            DayLength = 40000,
            SunriseUtc = "2025-01-01T06:00:00Z",
            SunsetUtc = "2025-01-01T18:00:00Z",
            SolarNoonUtc = "2025-01-01T12:00:00Z",
            CivilTwilightBeginUtc = null, CivilTwilightEndUtc = null,
            NauticalTwilightBeginUtc = null, NauticalTwilightEndUtc = null,
            AstronomicalTwilightBeginUtc = null, AstronomicalTwilightEndUtc = null
        ))
        val solarTime = solarTimeDao.getSolarTime(location.Id, today)!!

        // --- Create ---
        val alarm = SolarAlarm(
            Active = true,
            Name = "Morning Sunrise Alarm",
            LocationId = location.Id,
            SolarTimeId = solarTime.Id,
            Recurring = true,
            Monday = true, Tuesday = true, Wednesday = true, Thursday = true, Friday = true,
            Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.Before,
            SolarTimeTypeId = SolarTimeTypeEnum.Sunrise,
            OffsetHours = 0,
            OffsetMinutes = 30
        )
        solarAlarmDao.insert(alarm)

        // --- Read ---
        assertTrue(solarAlarmDao.isSolarAlarmNameLocationIdPairExists("Morning Sunrise Alarm", location.Id))
        val allAlarms = solarAlarmDao.getAll().first()
        assertEquals(1, allAlarms.size)

        val insertedAlarm = allAlarms[0]
        assertEquals("Morning Sunrise Alarm", insertedAlarm.Name)
        assertEquals(30, insertedAlarm.OffsetMinutes)

        // Read with details
        val details = solarAlarmDao.getAllWithDetails().first()
        assertEquals(1, details.size)
        assertEquals("Tokyo", details[0].location.Name)
        assertEquals(insertedAlarm.Id, details[0].solarAlarm.Id)

        // --- Update ---
        val updatedAlarm = insertedAlarm.copy(
            Name = "Updated Sunrise Alarm",
            OffsetMinutes = 45,
            Recurring = false
        ).apply { Id = insertedAlarm.Id }

        solarAlarmDao.update(updatedAlarm)

        val fetchedUpdated = solarAlarmDao.getById(insertedAlarm.Id)
        assertNotNull(fetchedUpdated)
        assertEquals("Updated Sunrise Alarm", fetchedUpdated?.Name)
        assertEquals(45, fetchedUpdated?.OffsetMinutes)
        assertFalse(fetchedUpdated?.Recurring == true)

        // --- Delete ---
        solarAlarmDao.delete(fetchedUpdated!!)
        assertNull(solarAlarmDao.getById(insertedAlarm.Id))
    }

    @Test
    fun testCascadeDelete() = runBlocking {
        locationDao.insert(Location(Id = 0, Name = "London", Latitude = 51.5074, Longitude = -0.1278))
        val location = locationDao.getByName("London")

        val today = LocalDate.now()
        solarTimeDao.insert(SolarTime(
            SolarDate = today, LocationId = location.Id, DayLength = 30000,
            SunriseUtc = "2025-01-01T08:00:00Z", SunsetUtc = "2025-01-01T16:00:00Z", SolarNoonUtc = "2025-01-01T12:00:00Z",
            CivilTwilightBeginUtc = null, CivilTwilightEndUtc = null, NauticalTwilightBeginUtc = null,
            NauticalTwilightEndUtc = null, AstronomicalTwilightBeginUtc = null, AstronomicalTwilightEndUtc = null
        ))
        val solarTime = solarTimeDao.getSolarTime(location.Id, today)!!

        solarAlarmDao.insert(SolarAlarm(
            Active = true, Name = "London Noon Alarm", LocationId = location.Id, SolarTimeId = solarTime.Id,
            Recurring = false, Monday = false, Tuesday = false, Wednesday = false, Thursday = false, Friday = false, Saturday = false, Sunday = false,
            OffsetTypeId = OffsetTypeEnum.At, SolarTimeTypeId = SolarTimeTypeEnum.SolarNoon, OffsetHours = 0, OffsetMinutes = 0
        ))

        assertEquals(1, solarAlarmDao.getAll().first().size)

        // Delete parent location (triggers CASCADE delete on SolarTime and SolarAlarm)
        locationDao.delete(location)

        assertEquals(0, solarAlarmDao.getAll().first().size)
        assertNull(solarTimeDao.getSolarTime(location.Id, today))
    }
}

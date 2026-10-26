package com.example.solar_alarm

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.sunrise_sunset_http.Results
import com.example.solar_alarm.sunrise_sunset_http.SolarRemoteDataSource
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse
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
class SolarTimeRepositoryTest {

    private lateinit var database: SolarAlarmDatabase
    private lateinit var locationDao: LocationDao
    private lateinit var solarTimeDao: SolarTimeDao
    private lateinit var fakeRemoteDataSource: FakeSolarRemoteDataSource
    private lateinit var repository: SolarTimeRepository

    private val testLocation = Location(
        Id = 1,
        Name = "New York",
        Latitude = 40.7128,
        Longitude = -74.0060
    )

    private val testDate = LocalDate.of(2026, 10, 26)

    class FakeSolarRemoteDataSource : SolarRemoteDataSource {
        var callCount = 0
        var lastRequest: SunriseSunsetRequest? = null
        var responseToReturn: SunriseSunsetResponse = SunriseSunsetResponse(
            results = Results(
                sunrise = "2026-10-26T11:20:00Z",
                sunset = "2026-10-26T22:00:00Z",
                solar_noon = "2026-10-26T16:40:00Z",
                day_length = 38400,
                civil_twilight_begin = "2026-10-26T10:50:00Z",
                civil_twilight_end = "2026-10-26T22:30:00Z",
                nautical_twilight_begin = "2026-10-26T10:15:00Z",
                nautical_twilight_end = "2026-10-26T23:05:00Z",
                astronomical_twilight_begin = "2026-10-26T09:40:00Z",
                astronomical_twilight_end = "2026-10-26T23:40:00Z"
            ),
            status = "OK"
        )

        override suspend fun getSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse {
            callCount++
            lastRequest = sunriseSunsetRequest
            return responseToReturn
        }
    }

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SolarAlarmDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        locationDao = database.locationDao()
        solarTimeDao = database.solarTimeDao()

        locationDao.insert(testLocation)

        fakeRemoteDataSource = FakeSolarRemoteDataSource()
        repository = SolarTimeRepository(solarTimeDao, fakeRemoteDataSource)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getSolarTime_whenCachedInDatabase_returnsFromDatabaseWithoutCallingRemote() = runBlocking {
        // Pre-insert into Room DB
        val existingSolarTime = SolarTime(
            SolarDate = testDate,
            LocationId = testLocation.Id,
            DayLength = 40000,
            SunriseUtc = "2026-10-26T11:00:00Z",
            SunsetUtc = "2026-10-26T22:00:00Z",
            SolarNoonUtc = "2026-10-26T16:30:00Z",
            CivilTwilightBeginUtc = null,
            CivilTwilightEndUtc = null,
            NauticalTwilightBeginUtc = null,
            NauticalTwilightEndUtc = null,
            AstronomicalTwilightBeginUtc = null,
            AstronomicalTwilightEndUtc = null
        )
        solarTimeDao.insert(existingSolarTime)

        val result = repository.getSolarTime(testLocation, testDate)

        assertNotNull(result)
        assertEquals("2026-10-26T11:00:00Z", result?.SunriseUtc)
        assertEquals(0, fakeRemoteDataSource.callCount) // Inversion of control allows verifying no network call was made
    }

    @Test
    fun getSolarTime_whenNotCached_fetchesFromRemoteDataSourceAndPersistsInDatabase() = runBlocking {
        // DB is empty initially
        val result = repository.getSolarTime(testLocation, testDate)

        assertNotNull(result)
        assertEquals(1, fakeRemoteDataSource.callCount)
        assertEquals(testLocation.Latitude.toFloat(), fakeRemoteDataSource.lastRequest?.Latitude)
        assertEquals(testLocation.Longitude.toFloat(), fakeRemoteDataSource.lastRequest?.Longitude)
        assertEquals(testDate, fakeRemoteDataSource.lastRequest?.RequestDate)

        // Verify result contains data from remote
        assertEquals("2026-10-26T11:20:00Z", result?.SunriseUtc)
        assertEquals(38400, result?.DayLength)

        // Verify data was persisted in database
        val cachedInDb = solarTimeDao.getSolarTime(testLocation.Id, testDate)
        assertNotNull(cachedInDb)
        assertEquals("2026-10-26T11:20:00Z", cachedInDb?.SunriseUtc)
    }

    @Test
    fun getSolarTime_whenRemoteFails_returnsNull() = runBlocking {
        fakeRemoteDataSource.responseToReturn = SunriseSunsetResponse(results = null, status = "INVALID_REQUEST")

        val result = repository.getSolarTime(testLocation, testDate)

        assertNull(result)
        assertEquals(1, fakeRemoteDataSource.callCount)
    }
}

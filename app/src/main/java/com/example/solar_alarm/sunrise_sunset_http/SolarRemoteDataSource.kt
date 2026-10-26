package com.example.solar_alarm.sunrise_sunset_http

import java.io.IOException

/**
 * Interface defining the remote data source for fetching solar time data.
 * Allows decoupling network logic from repositories and enables unit testing with fakes/mocks.
 */
interface SolarRemoteDataSource {
    @Throws(IOException::class)
    suspend fun getSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
}

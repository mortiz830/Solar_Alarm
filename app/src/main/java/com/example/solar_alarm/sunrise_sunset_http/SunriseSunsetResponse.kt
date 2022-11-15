package com.example.solar_alarm.sunrise_sunset_http

class SunriseSunsetResponse {
    var results: Results? = null
    @kotlin.jvm.JvmField
    var status: String? = null
    @kotlin.jvm.JvmField
    var request: SunriseSunsetRequest? = null
    val sunrise: String?
        get() = results.getSunrise()
    val sunset: String?
        get() = results.getSunset()
    val solarNoon: String?
        get() = results.getSolar_noon()
    val dayLength: Int
        get() = results.getDay_length()
    val civilTwilightBegin: String?
        get() = results.getCivil_twilight_begin()
    val civilTwilightEnd: String?
        get() = results.getCivil_twilight_end()
    val nauticalTwilightBegin: String?
        get() = results.getNautical_twilight_begin()
    val nauticalTwilightEnd: String?
        get() = results.getNautical_twilight_end()
    val astronomicalTwilightBegin: String?
        get() = results.getAstronomical_twilight_begin()
    val astronomicalTwilightEnd: String?
        get() = results.getAstronomical_twilight_end()
}
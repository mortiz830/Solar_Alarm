package com.example.solar_alarm.sunrise_sunset_http

class SunriseSunsetResponse {
    var results: Results? = null
    @kotlin.jvm.JvmField
    var status: String? = null
    @kotlin.jvm.JvmField
    var request: SunriseSunsetRequest? = null
    val sunrise: String?
        get() = results?.sunrise
    val sunset: String?
        get() = results?.sunset
    val solarNoon: String?
        get() = results?.solar_noon
    val dayLength: Int?
        get() = results?.day_length
    val civilTwilightBegin: String?
        get() = results?.civil_twilight_begin
    val civilTwilightEnd: String?
        get() = results?.civil_twilight_end
    val nauticalTwilightBegin: String?
        get() = results?.nautical_twilight_begin
    val nauticalTwilightEnd: String?
        get() = results?.nautical_twilight_end
    val astronomicalTwilightBegin: String?
        get() = results?.astronomical_twilight_begin
    val astronomicalTwilightEnd: String?
        get() = results?.astronomical_twilight_end
}
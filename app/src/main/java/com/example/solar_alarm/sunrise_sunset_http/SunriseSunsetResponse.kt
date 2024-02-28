package com.example.solar_alarm.sunrise_sunset_http

data class SunriseSunsetResponse (
    var results: Results? = null,
    var status: String? = null,
    var request: SunriseSunsetRequest? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val solarNoon: String? = null,
    val dayLength: Int? = null,
    val civilTwilightBegin: String? = null,
    val civilTwilightEnd: String? = null,
    val nauticalTwilightBegin: String? = null,
    val nauticalTwilightEnd: String? = null,
    val astronomicalTwilightBegin: String? = null,
    val astronomicalTwilightEnd: String? = null
)
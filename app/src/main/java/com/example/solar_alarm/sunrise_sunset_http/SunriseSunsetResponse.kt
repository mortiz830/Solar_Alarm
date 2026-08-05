package com.example.solar_alarm.sunrise_sunset_http

// Fixed broken line
data class SunriseSunsetResponse (
    var results: Results? = null,
    var status: String? = null,
    var tzid: String? = null,
    var request: SunriseSunsetRequest? = null
)
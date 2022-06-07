package com.example.solar_alarm.sunrise_sunset_http;

public class Results {
    private String sunrise;
    private String sunset;
    private String solar_noon;
    private int day_length;
    private String civil_twilight_begin;
    private String civil_twilight_end;
    private String nautical_twilight_begin;
    private String nautical_twilight_end;
    private String astronomical_twilight_begin;
    private String astronomical_twilight_end;

    public Results(String sunrise, String sunset, String solar_noon, int day_length, String ctb, String cte, String ntb, String nte, String atb, String ate)
    {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.solar_noon = solar_noon;
        this.day_length = day_length;
        this.civil_twilight_begin = ctb;
        this.civil_twilight_end = cte;
        this.nautical_twilight_begin = ntb;
        this.nautical_twilight_end = nte;
        this.astronomical_twilight_begin = atb;
        this.astronomical_twilight_end = ate;
    }

    public String getSunrise() { return sunrise; }

    public String getSunset() { return sunset; }

    public String getSolar_noon() { return solar_noon; }

    public int getDay_length() { return day_length; }

    public String getCivil_twilight_begin() { return civil_twilight_begin; }

    public String getCivil_twilight_end() { return civil_twilight_end; }

    public String getNautical_twilight_begin() { return nautical_twilight_begin; }

    public String getNautical_twilight_end() { return nautical_twilight_end; }

    public String getAstronomical_twilight_begin() { return astronomical_twilight_begin; }

    public String getAstronomical_twilight_end() { return astronomical_twilight_end; }
}

/*
{
    "results": {
        "sunrise": "11:48:41 AM",
        "sunset": "1:02:48 AM",
        "solar_noon": "6:25:45 PM",
        "day_length": 01234567,
        "civil_twilight_begin": "11:22:54 AM",
        "civil_twilight_end": "1:28:36 AM",
        "nautical_twilight_begin": "10:52:06 AM",
        "nautical_twilight_end": "1:59:23 AM",
        "astronomical_twilight_begin": "10:20:08 AM",
        "astronomical_twilight_end": "2:31:22 AM"
    },
    "status": "OK"
}
 */
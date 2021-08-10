package com.example.solar_alarm.Location;

public class TimeZoneResults {

    private String status;
    private String message;
    private String countryCode;
    private String countryName;
    private String zoneName;
    private String abbreviation;
    private int gmtOffset;
    private String dst;
    private int zoneStart;
    private int zoneEnd;
    private String nextAbbreviation;
    private int timestamp;
    private String formatted;

    public TimeZoneResults(String status, String message, String countryCode, String countryName, String zoneName,
                                String abbreviation, int gmtOffset, String dst, int zoneStart, int zoneEnd, String nextAbbreviation,
                                int timestamp, String formatted)
    {
        this.status = status;
        this.message = message;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.zoneName = zoneName;
        this.abbreviation = abbreviation;
        this.gmtOffset = gmtOffset;
        this.dst = dst;
        this.zoneStart = zoneStart;
        this.zoneEnd = zoneEnd;
        this.nextAbbreviation = nextAbbreviation;
        this.timestamp = timestamp;
        this.formatted = formatted;
    }

    public String getZoneName() {return zoneName; }
}

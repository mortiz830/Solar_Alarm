package com.example.solar_alarm.Location;

public class TimeZoneResults {

    public String status;
    public String message;
    public String countryCode;
    public String countryName;
    public String zoneName;
    public String abbreviation;
    public int gmtOffset;
    public boolean dst;
    public int zoneStart;
    public int zoneEnd;
    public String nextAbbreviation;
    public int timestamp;
    public String formatted;

    public TimeZoneResults(String status, String message, String countryCode, String countryName, String zoneName,
                                String abbreviation, int gmtOffset, boolean dst, int zoneStart, int zoneEnd, String nextAbbreviation,
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

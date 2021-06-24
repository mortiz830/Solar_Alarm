package com.example.solar_alarm.Location;

public class TimeZoneResults {

    private int dstOffset;
    private int rawOffset;
    private String status;
    private String timeZoneId;
    private String timeZoneName;

    public TimeZoneResults(int dstOffset, int rawOffset, String status, String timeZoneId, String timeZoneName)
    {
        this.dstOffset = dstOffset;
        this.rawOffset = rawOffset;
        this.status = status;
        this.timeZoneId = timeZoneId;
        this.timeZoneName = timeZoneName;
    }

    public void setDstOffset(int dstOffset) {this.dstOffset = dstOffset; }

    public void setRawOffset(int rawOffset) {this.rawOffset = rawOffset; }

    public void setStatus(String status) {this.status = status; }

    public void setTimeZoneId(String timeZoneId) {this.timeZoneId = timeZoneId; }

    public void setTimeZoneName(String timeZoneName) {this.timeZoneName = timeZoneName; }

    public int getDstOffset() {return dstOffset; }

    public int getRawOffset() {return rawOffset; }

    public String getStatus() {return status; }

    public String getTimeZoneId() {return timeZoneId; }

    public String getTimeZoneName() {return timeZoneName; }
}

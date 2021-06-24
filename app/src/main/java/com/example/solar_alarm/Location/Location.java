package com.example.solar_alarm.Location;

public class Location {
    private int locationID;
    private double latitude;
    private double longitude;
    private String locationName;
    private String timeZoneID;

    private long created;
    private long updated;

    public Location(int locationID, double latitude, double longitude, String locationName,
                    String timeZoneID, long created)
    {
        this.locationID = locationID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.timeZoneID = timeZoneID;
        this.created = created;
        updated = 0;
    }

    public void updateLocation(int locationID, double latitude, double longitude, String locationName,
                          String timeZoneID, long updated)
    {
        this.locationID = locationID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.timeZoneID = timeZoneID;
        this.updated = updated;
    }

    public void setLocationID(int locationID) {this.locationID = locationID; }

    public void setLatitude(double latitude) {this.latitude = latitude; }

    public void setLongitude(double longitude) {this.longitude = longitude; }

    public void setLocationName(String locationName) {this.locationName = locationName; }

    public void setTimeZoneID(String timeZoneID) {this.timeZoneID = timeZoneID; }

    public void setCreated(long created) {this.created = created; }

    public void setUpdated(long updated) {this.updated = updated; }

    public int getLocationID() { return locationID; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getLocationName() { return locationName; }

    public String getTimeZoneID() { return timeZoneID; }

    public long getCreated() { return created; }

    public long getUpdated() { return updated; }
}

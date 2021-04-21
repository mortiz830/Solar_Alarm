package com.example.solar_alarm;

import com.example.solar_alarm.SolarCalculator.Location;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertTrue;

public class BaseTestCase {

    protected Calendar eventDate;

    protected Location location;

    public void setup(int month, int day, int year) {
        //this.setup(month, day, year, "39.9937", "-75.7850", "America/New_York");
        this.setup(month, day, year, "33.2539", "-96.8097", "America/Chicago");
    }

    public void setup(int month, int day, int year, String longitude, String latitude, String timeZoneIdentifier) {
        eventDate = Calendar.getInstance();
        eventDate.set(Calendar.YEAR, year);
        eventDate.set(Calendar.MONTH, month);
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.setTimeZone(TimeZone.getTimeZone(timeZoneIdentifier));
        location = new Location(longitude, latitude);
    }

    @Test
    public void testTrue() {
        assertTrue(true);
    }

    /**
     * +- one minute is good enough.
     *
     * @param expectedTime
     * @param actualTime
     * @return
     */
    protected void assertTimeEquals(String expectedTime, String actualTime, String date) {
        int expectedMinutes = getMinutes(expectedTime);
        int actualMinutes = getMinutes(actualTime);

        if (((expectedMinutes - 1) <= actualMinutes) && (actualMinutes <= (expectedMinutes + 1))) {
            return;
        }
        Assert.fail("Expected: " + expectedTime + ", but was: " + actualTime + " for date: " + date);
    }

    protected String getMessage(Object expected, Object actual) {
        return "Expected: " + expected + " but was: " + actual;
    }

    private int getMinutes(String timeString) {
        String[] timeParts = timeString.split("\\:");
        if (timeParts[0].equals("00")) {
            timeParts[0] = "24";
        }
        return (60 * Integer.valueOf(timeParts[0])) + Integer.valueOf(timeParts[1]);
    }
}

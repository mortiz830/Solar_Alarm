package com.example.solar_alarm;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.solar_alarm.Data.Daos.LocationDao;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.Location;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LocationTests
{
    private LocationDao locationDao;
    private SolarAlarmDatabase solarAlarmDatabase;

    @Before
    public void createDb()
    {
        Context context = ApplicationProvider.getApplicationContext();

        solarAlarmDatabase = Room.inMemoryDatabaseBuilder(context, SolarAlarmDatabase.class).build();
        locationDao        = solarAlarmDatabase.locationDao();
    }

    @After
    public void closeDb() throws IOException
    {
        solarAlarmDatabase.close();
    }

    @Test
    public void writeLocationAndReadInList() throws Exception
    {
        /*
        User user = TestUtil.createUser(3);
        user.setName("george");
        userDao.insert(user);
        List<User> byName = userDao.findUsersByName("george");
        assertThat(byName.get(0), equalTo(user));
        */
        Location newLocation = new Location();

        newLocation.Latitude = 33.00;
        newLocation.Longitude = 40.00;
        newLocation.Name = "TEST LOCATION";
        newLocation.TimezoneId = "America/Chicago";

        locationDao.Insert(newLocation);

        Location l = locationDao.getAll().getValue().iterator().next();



        Assert.assertSame(newLocation.Name, l.Name);

        //l.
    }
}

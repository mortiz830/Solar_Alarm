package com.example.solar_alarm;

import android.content.Context;

import androidx.room.Room;

import com.example.solar_alarm.Data.Repositories.LocationDao;
import com.example.solar_alarm.Data.SolarAlarmDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.IOException;

public class LocationTests
{
    @RunWith(AndroidJUnit4.class)
    public class SimpleEntityReadWriteTest {
        private LocationDao userDao;
        private SolarAlarmDatabase db;

        @Before
        public void createDb() {
            Context context = ApplicationProvider.getApplicationContext();
            db = Room.inMemoryDatabaseBuilder(context, SolarAlarmDatabase.class).build();
            userDao = db.locationDao();
        }

        @After
        public void closeDb() throws IOException
        {
            db.close();
        }

        /*@Test
        public void writeUserAndReadInList() throws Exception {
            User user = TestUtil.createUser(3);
            user.setName("george");
            userDao.insert(user);
            List<User> byName = userDao.findUsersByName("george");
            assertThat(byName.get(0), equalTo(user));
        }*/
    }
}

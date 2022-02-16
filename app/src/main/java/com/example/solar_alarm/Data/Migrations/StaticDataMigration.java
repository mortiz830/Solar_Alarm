package com.example.solar_alarm.Data.Migrations;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.solar_alarm.Data.Enums.AlarmTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Enums.TimeUnitTypeEnum;
import com.example.solar_alarm.Data.Tables.AlarmType;
import com.example.solar_alarm.Data.Tables.SolarTimeType;
import com.example.solar_alarm.Data.Tables.TimeUnitType;

@Database(entities = {TimeUnitType.class, AlarmType.class, SolarTimeType.class}, version = 2, exportSchema = false)
public abstract class StaticDataMigration extends RoomDatabase
{
    static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.beginTransaction();

            String sql = "INSERT INTO TimeUnitTypes VALUES ";

            for (TimeUnitTypeEnum enumType : TimeUnitTypeEnum.values())
            {
                sql = sql + String.format("(%d, %s),", enumType.Id, enumType.Name);
            }

            sql = removeLastChar(sql);
            database.execSQL(sql);

            //--------------------------

            sql = "INSERT INTO AlarmTypes VALUES ";

            for (AlarmTypeEnum enumType : AlarmTypeEnum.values())
            {
                sql = sql + String.format("(%d, %s),", enumType.Id, enumType.Name);
            }

            sql = removeLastChar(sql);
            database.execSQL(sql);

            //--------------------------

            sql = "INSERT INTO SolarTimeTypes VALUES ";

            for (SolarTimeTypeEnum enumType : SolarTimeTypeEnum.values())
            {
                sql = sql + String.format("(%d, %s),", enumType.Id, enumType.Name);
            }

            sql = removeLastChar(sql);
            database.execSQL(sql);

            database.endTransaction();
        }

        //Room.databaseBuilder(Context, SolarAlarmDatabase, "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
        //Database.mig(Context, SolarAlarmDatabase., "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
    };

    private static String removeLastChar(String s)
    {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }
}
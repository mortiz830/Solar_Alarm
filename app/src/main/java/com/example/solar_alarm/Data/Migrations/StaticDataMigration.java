package com.example.solar_alarm.Data.Migrations;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Tables.OffsetType;
import com.example.solar_alarm.Data.Tables.SolarTimeType;

@RequiresApi(api = Build.VERSION_CODES.O)
@Database
(
    entities =
    {
        OffsetType.class,
        SolarTimeType.class
    },
    version = 2,
    exportSchema = false
)

public abstract class StaticDataMigration extends RoomDatabase
{
    public static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.beginTransaction();

            String sql = "INSERT INTO OffsetTypes VALUES ";

            for (OffsetTypeEnum enumType : OffsetTypeEnum.values())
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

            //Room.databaseBuilder(getApplicationContext(), SolarAlarmDatabase.class, "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
        }

        //Room.databaseBuilder(Context, SolarAlarmDatabase.class, "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
        //Database.mig(Context, SolarAlarmDatabase., "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
    };

    private static String removeLastChar(String s)
    {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }
}
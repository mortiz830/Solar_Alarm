//package com.example.solar_alarm.Data.Migrations
//
//import androidx.annotation.RequiresApi
//import android.os.Build
//import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
//import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
//import com.example.solar_alarm.Data.Tables.OffsetType
//import com.example.solar_alarm.Data.Tables.SolarTimeType
//import androidx.room.Database
//import androidx.room.RoomDatabase
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
//
//@RequiresApi(api = Build.VERSION_CODES.O)
//@Database(entities = [OffsetType::class, SolarTimeType::class], version = 2, exportSchema = false)
//abstract class StaticDataMigration : RoomDatabase() {
//    companion object {
//        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.beginTransaction()
//                var sql: String? = "INSERT INTO OffsetTypes VALUES "
//                for (enumType in OffsetTypeEnum.values()) {
//                    sql = sql + String.format("(%d, %s),", enumType.Id, enumType.Name)
//                }
//                sql = removeLastChar(sql)
//                database.execSQL(sql)
//
//                //--------------------------
//                sql = "INSERT INTO SolarTimeTypes VALUES "
//                for (enumType in SolarTimeTypeEnum.values()) {
//                    sql = sql + String.format("(%d, %s),", enumType.Id, enumType.Name)
//                }
//                sql = removeLastChar(sql)
//                database.execSQL(sql)
//                database.endTransaction()
//
//                //Room.databaseBuilder(getApplicationContext(), SolarAlarmDatabase.class, "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
//            } //Room.databaseBuilder(Context, SolarAlarmDatabase.class, "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
//            //Database.mig(Context, SolarAlarmDatabase., "SolarAlarmDatabase").addMigrations(MIGRATION_1_2).build();
//        }
//
//        private fun removeLastChar(s: String?): String? {
//            return if (s == null || s.length == 0) null else s.substring(0, s.length - 1)
//        }
//    }
//}
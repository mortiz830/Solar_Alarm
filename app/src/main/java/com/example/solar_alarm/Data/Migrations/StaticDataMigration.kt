package com.example.solar_alarm.data.migrations

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum

abstract class StaticDataMigration : RoomDatabase() {
    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    var sql = "INSERT INTO OffsetTypes VALUES "
                    for (enumType in OffsetTypeEnum.values()) {
                        sql += String.format("(%d, '%s'),", enumType.Id, enumType.Name)
                    }
                    database.execSQL(sql.removeSuffix(","))

                    sql = "INSERT INTO SolarTimeTypes VALUES "
                    for (enumType in SolarTimeTypeEnum.values()) {
                        sql += String.format("(%d, '%s'),", enumType.Id, enumType.Name)
                    }
                    database.execSQL(sql.removeSuffix(","))
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }
    }
}

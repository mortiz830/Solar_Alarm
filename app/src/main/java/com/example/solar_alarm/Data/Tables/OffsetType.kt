package com.example.solar_alarm.Data.Tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
(
    tableName = "OffsetType",
    indices   = [Index(value = ["Name"], unique = true)]
)

data class OffsetType
(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") var Id: Int = 0,
    @ColumnInfo(name = "Name") var Name: String
)
{
    companion object { const val TABLE_NAME = "OffsetType" }
}

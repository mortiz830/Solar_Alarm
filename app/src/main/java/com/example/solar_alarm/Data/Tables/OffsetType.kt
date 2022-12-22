package com.example.solar_alarm.Data.Tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.solar_alarm.Data.Tables.TableBase

@Entity(indices = [Index(value = ["Name"], unique = true)])

class OffsetType
(
    @PrimaryKey(autoGenerate = true) val Id: Int,

    @ColumnInfo(name = "Name") val Name: String?
)
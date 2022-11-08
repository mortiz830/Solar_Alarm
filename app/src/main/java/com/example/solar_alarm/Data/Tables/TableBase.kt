package com.example.solar_alarm.Data.Tables

import androidx.room.PrimaryKey

abstract class TableBase {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var Id = 0 //    @NonNull
    //    public LocalDateTime Created;
    //
    //    @NonNull
    //    public LocalDateTime Updated;
}
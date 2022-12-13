package com.example.solar_alarm.Data.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
abstract class TableBase {
    //@PrimaryKey(autoGenerate = true)
    //var Id = 0 //    @NonNull
    @PrimaryKey(autoGenerate = true) val Id: Int
    //    public LocalDateTime Created;
    //
    //    @NonNull
    //    public LocalDateTime Updated;
}
package com.example.solar_alarm.Data.Tables;

import androidx.room.PrimaryKey;

public abstract class TableBase
{
    @PrimaryKey(autoGenerate = true)
    public int Id;

//    @NonNull
//    public LocalDateTime Created;
//
//    @NonNull
//    public LocalDateTime Updated;
}

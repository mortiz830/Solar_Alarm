package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

public abstract class TableBase
{
    @PrimaryKey
    public int Id;

    @NonNull
    public LocalDateTime Created;

    @NonNull
    public LocalDateTime Updated;
}

package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity
(
    tableName = "TimeTypes",
    indices = { @Index(value = {"Name"}, unique = true) }
)

public class TimeType extends TableBase
{
    @NonNull
    public String Name;
}

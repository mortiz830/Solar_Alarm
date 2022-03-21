package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity
(
    tableName = "AlarmTypes",
    indices = { @Index(value = {"Name"}, unique = true) }
)

public class AlarmType extends TableBase
{
    @NonNull
    public String Name;
}

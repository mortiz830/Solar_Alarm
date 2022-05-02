package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity
(
    tableName = "OffsetTypes",
    indices = { @Index(value = {"Name"}, unique = true) }
)

public class OffsetType extends TableBase
{
    @NonNull
    public String Name;
}

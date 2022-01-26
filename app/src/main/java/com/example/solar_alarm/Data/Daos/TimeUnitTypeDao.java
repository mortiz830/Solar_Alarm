package com.example.solar_alarm.Data.Daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.solar_alarm.Data.Tables.TimeUnitType;

@Dao
public interface TimeUnitTypeDao
{
    @Insert
    void Insert(TimeUnitType timeUnitType);
}

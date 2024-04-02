//package com.example.solar_alarm.Data.Tables
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import java.time.OffsetDateTime
//import java.time.ZoneOffset
//
//@Entity
//abstract class TableBase
//{
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "Id")
//    var Id: Int = 0
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    @ColumnInfo(name = "CreateDateTimeUtc")
//    var CreateDateTimeUtc : OffsetDateTime = OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC)
//}
//
//@Entity
//abstract class TableNameBase(val name : String) : TableBase()
//{
//    @ColumnInfo(name = "Name")
//    var Name: String = name
//}
package com.example.solar_alarm.data.enums

// Repair: Fixed broken package/import lines
enum class OffsetTypeEnum(val Id: Int, val Name: String)
{
    Before(1, "Before"),
    At    (2, "At"),
    After (3, "After");
}
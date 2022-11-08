package com.example.solar_alarm.Data.Tables

import androidx.room.Entity
import androidx.room.Index
import com.example.solar_alarm.Data.Tables.TableBase

@Entity(tableName = "Timezone", indices = [Index(value = ["ZoneName"], unique = true)])
class Timezone : TableBase() {
    var CountryCode: String? = null
    var CountryName: String? = null
    var ZoneName: String? = null
    var Abbreviation: String? = null
    var GmtOffset = 0
    var Dst: Boolean? = null
    var ZoneStart = 0
    var ZoneEnd = 0
    var NextAbbreviation: String? = null
    var Timestamp = 0
}
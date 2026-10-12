# Database Entity-Relationship Diagram (ERD)

This document describes the database schema and relationships for the Solar Alarm application (`SolarAlarmDatabase`).

## Entity-Relationship Diagram

```mermaid
erDiagram
    LOCATION ||--o{ SOLAR_TIME : "has calculated daily times"
    LOCATION ||--o{ SOLAR_ALARM : "contains scheduled alarms"
    SOLAR_TIME ||--o{ SOLAR_ALARM : "provides astronomical calculation"

    LOCATION {
        int Id PK
        string Name "Unique location name"
        double Latitude "Latitude coordinate"
        double Longitude "Longitude coordinate"
        datetime CreateDateTimeUtc "Creation timestamp"
    }

    SOLAR_TIME {
        int Id PK
        date SolarDate "Target date"
        int LocationId FK "Foreign key to Location.Id"
        int DayLength "Day duration in seconds"
        string SunriseUtc "Sunrise UTC timestamp"
        string SunsetUtc "Sunset UTC timestamp"
        string SolarNoonUtc "Solar Noon UTC timestamp"
        string CivilTwilightBeginUtc "Civil twilight start UTC"
        string CivilTwilightEndUtc "Civil twilight end UTC"
        string NauticalTwilightBeginUtc "Nautical twilight start UTC"
        string NauticalTwilightEndUtc "Nautical twilight end UTC"
        string AstronomicalTwilightBeginUtc "Astronomical twilight start UTC"
        string AstronomicalTwilightEndUtc "Astronomical twilight end UTC"
        datetime CreateDateTimeUtc "Creation timestamp"
    }

    SOLAR_ALARM {
        int Id PK
        boolean Active "Alarm active status"
        string Name "Alarm name"
        int LocationId FK "Foreign key to Location.Id"
        int SolarTimeId FK "Foreign key to SolarTime.Id"
        boolean Recurring "Is recurring alarm"
        boolean Monday "Active on Monday"
        boolean Tuesday "Active on Tuesday"
        boolean Wednesday "Active on Wednesday"
        boolean Thursday "Active on Thursday"
        boolean Friday "Active on Friday"
        boolean Saturday "Active on Saturday"
        boolean Sunday "Active on Sunday"
        enum OffsetTypeId "Exact, Before, After"
        enum SolarTimeTypeId "Sunrise, SolarNoon, Sunset, etc."
        int OffsetHours "Hours offset"
        int OffsetMinutes "Minutes offset"
        datetime CreateDateTimeUtc "Creation timestamp"
    }
```

## Schema Details

### Tables & Relationships

1. **`LOCATION`**
   - Stores saved user locations (latitude/longitude coordinates).
   - Has a one-to-many relationship with `SOLAR_TIME` (each location can have multiple calculated daily solar events).
   - Has a one-to-many relationship with `SOLAR_ALARM` (multiple alarms can be tied to a location).

2. **`SOLAR_TIME`**
   - Caches calculated solar/astronomical event times (sunrise, solar noon, sunset, twilights) for a specific location and date.
   - Referenced by `SOLAR_ALARM` to calculate precise trigger times.

3. **`SOLAR_ALARM`**
   - Defines scheduled solar alarms, including recurrence flags, solar event target (`SolarTimeTypeId`), and time offsets (`OffsetTypeId`, `OffsetHours`, `OffsetMinutes`).

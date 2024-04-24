# Soalr Alarm

This is an Android alarm app that allows users to set alarms based on the sun. Alarms can be set before, at, or after sunrise, solar noon, of sunset.

## Developer Guide

### How to Access the Database

#### Install DB Browser for SQLite

[Downloads - DB Browser for SQLite](https://sqlitebrowser.org/dl/)

![DB Browser for SQLite](.\images\image-1.png "DB Browser for SQLite")

#### Find the Database Files in the Device Explorer

- Navigate to this path: `/data/data/com.example.solar_alarm/databases`
- Select all 3 files and save them to the local machine
  - `SolarAlarmDatabase`
  - `SolarAlarmDatabase-shm`
  - `SolarAlarmDatabase-wal`

![Device Explorer](.\images\image-3.png "Device Explorer")

#### Open the Database Files with DB Browser for SQLite

- Open DB Browser for SQLite
- File -> Open Database...
- Select `SolarAlarmDatabase`. The file with no extension.

![File Selection](.\images\image-4.png "File Selection")

![DB View](.\images\image-5.png "DB View")

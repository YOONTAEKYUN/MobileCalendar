package com.example.mobilecalendar.roomdb.converters

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromTime(value: LocalTime): String {
        return value.toString()
    }

    @TypeConverter
    fun toTime(value: String): LocalTime {
        return LocalTime.parse(value)
    }
}
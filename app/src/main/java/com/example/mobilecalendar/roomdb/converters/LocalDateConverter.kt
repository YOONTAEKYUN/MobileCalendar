package com.example.mobilecalendar.roomdb.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun fromDate(value: LocalDate): Long {
        return value.toEpochDay()
    }

    @TypeConverter
    fun toDate(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }
}
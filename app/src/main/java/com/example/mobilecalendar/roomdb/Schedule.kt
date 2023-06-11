package com.example.mobilecalendar.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Schedule (
    val title: String,
    val date: LocalDate,
    val place: String,
    val start_time: LocalTime,
    val end_time: LocalTime
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
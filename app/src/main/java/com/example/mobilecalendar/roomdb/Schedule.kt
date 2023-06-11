package com.example.mobilecalendar.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Schedule (
    var title: String,
    val date: LocalDate,
    var place: String,
    var start_time: LocalTime,
    var end_time: LocalTime
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
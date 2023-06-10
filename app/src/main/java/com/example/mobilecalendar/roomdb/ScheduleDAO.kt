package com.example.mobilecalendar.roomdb

import androidx.room.*
import java.time.LocalDate

@Dao
interface ScheduleDAO {
    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    suspend fun getAllSchedules(): List<Schedule>

    @Query("SELECT * FROM Schedule WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Int): Schedule

    @Query("SELECT * FROM Schedule WHERE date = :date")
    suspend fun getSchedulesByDate(date: LocalDate): List<Schedule>
}
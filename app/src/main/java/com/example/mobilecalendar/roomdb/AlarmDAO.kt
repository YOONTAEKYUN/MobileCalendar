package com.example.mobilecalendar.roomdb

import androidx.room.*

@Dao
interface AlarmDAO {
    @Insert
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM Alarm WHERE scheduleId = :scheduleId")
    suspend fun getAlarmForSchedule(scheduleId: Long): List<Alarm>

}
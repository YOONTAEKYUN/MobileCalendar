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
    suspend fun getAlarmForSchedule(scheduleId: Long): Alarm
    @Query("UPDATE Alarm SET message = :newMessage WHERE id = :alarmId")
    suspend fun updateAlarmMessage(alarmId: Int, newMessage: String)

}
package com.example.mobilecalendar.roomdb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Alarm(
    val scheduleId: Int,
    val title: String,
    val message: String,
    val time: LocalTime,
    val interval: Int //알림 반복 주기 (시간 단위)
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

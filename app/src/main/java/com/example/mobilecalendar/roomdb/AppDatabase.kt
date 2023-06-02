package com.example.mobilecalendar.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mobilecalendar.roomdb.converters.LocalDateConverter
import com.example.mobilecalendar.roomdb.converters.LocalTimeConverter

@Database(entities = [Schedule::class, Alarm::class], version = 1)
@TypeConverters(LocalDateConverter::class, LocalTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDAO
    abstract fun AlarmDAO(): AlarmDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { instance = it }
            }
        }
    }
}

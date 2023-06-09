package com.example.mobilecalendar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobilecalendar.dao.TodoDao
import com.example.mobilecalendar.dto.Todo

@Database(entities = arrayOf(Todo::class), version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
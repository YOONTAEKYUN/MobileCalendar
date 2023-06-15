package com.example.mobilecalendar.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.mobilecalendar.database.TodoDatabase
import com.example.mobilecalendar.dto.Todo

private const val DATABASE_NAME = "todo-database.db"
class TodoRepository private constructor(context: Context){

    private val database: TodoDatabase = Room.databaseBuilder(
        context.applicationContext,
        TodoDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val todoDao = database.todoDao()

    fun list(): LiveData<MutableList<Todo>> = todoDao.list()

    fun getTodo(id: Long): Todo = todoDao.selectOne(id)

    fun insert(dto: Todo) = todoDao.insert(dto)

    suspend fun update(dto: Todo) = todoDao.update(dto)

    fun delete(dto: Todo) = todoDao.delete(dto)

    companion object {
        private var INSTANCE: TodoRepository?=null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TodoRepository(context)
            }
        }

        fun get(): TodoRepository {
            // INSTANCE의 상태를 로그로 출력
            Log.d("TodoRepository", "INSTANCE: $INSTANCE")
            return INSTANCE ?:
            throw IllegalStateException("TodoRepository must be initialized")
        }
    }
}
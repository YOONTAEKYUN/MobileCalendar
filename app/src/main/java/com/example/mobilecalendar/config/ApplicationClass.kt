package com.example.mobilecalendar.config

import android.app.Application
import com.example.mobilecalendar.repository.TodoRepository

class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()

        TodoRepository.initialize(this)
    }
}

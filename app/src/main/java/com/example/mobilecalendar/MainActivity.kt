package com.example.mobilecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.mobilecalendar.databinding.ActivityMainBinding
import com.example.mobilecalendar.monthCalendar.MonthFrag
import com.example.mobilecalendar.weekCalendar.WeekFrag


class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.month.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_view, MonthFrag())
                .commit()
        }
        binding.week.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_view, WeekFrag())
                .commit()
        }
        binding.todo.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_view, TodoMainActivity())
                .commit()
        }
    }
}




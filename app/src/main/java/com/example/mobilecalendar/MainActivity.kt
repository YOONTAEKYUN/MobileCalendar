package com.example.mobilecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobilecalendar.databinding.ActivityMainBinding


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
    }
}




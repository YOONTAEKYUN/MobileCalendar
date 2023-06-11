package com.example.mobilecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mobilecalendar.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

// 디버그 키 해시 : yzXmrPCP5gi9Nt0gJOplguL51Sc=
// 릴리즈 키 해시 : nZfccoI3KzCvTHynSMOxiSTBwXE=

import com.example.mobilecalendar.monthCalendar.MonthFrag
import com.example.mobilecalendar.weekCalendar.WeekFrag


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MainActivity", "Firebase Token: $token")
                // 토큰을 서버로 전송하거나 필요한 곳에 사용합니다.
            } else {
                Log.e("MainActivity", "Failed to get Firebase token")
            }
        })

//        // 카카오 api 사용을 위한 릴리즈 해시키 확인
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("hash", keyHash.toString())

    }
}



package com.example.mobilecalendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecalendar.databinding.ActivitySetAlarmBinding

class SetAlarmActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = findViewById<EditText>(R.id.inputAlarmContent)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            val userInput = inputEditText.text.toString()
            Log.d("Activity", userInput)
            // TODO: userInput를 처리하는 로직 추가
            // 예를 들면, 입력된 내용을 저장하거나 다른 동작을 수행할 수 있습니다.
            finish() // 작업이 완료되면 액티비티를 종료하여 이전 화면으로 돌아갈 수 있습니다.
        }
    }
}
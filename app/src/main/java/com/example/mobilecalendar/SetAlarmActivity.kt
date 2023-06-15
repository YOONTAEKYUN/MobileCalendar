package com.example.mobilecalendar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.mobilecalendar.databinding.ActivitySetAlarmBinding
import com.example.mobilecalendar.roomdb.Alarm
import com.example.mobilecalendar.roomdb.AppDatabase
import com.example.mobilecalendar.roomdb.Schedule
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale


class SetAlarmActivity : AppCompatActivity() {
    fun buildNotification(title: String, body: String) : Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "1234"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH) //알림을 화면 상단에 배너처럼 띄움
            .setSmallIcon(android.R.drawable.sym_def_app_icon) // 작은 아이콘 추가
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pIntent)
        //.setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        return notificationBuilder.build()

    }

    lateinit var binding: ActivitySetAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inputAlarmTitle = findViewById<EditText>(R.id.inputAlarmTitle)
        val inputAlarmLocation = findViewById<EditText>(R.id.inputAlarmLocation)
        val inputAlarmContent = findViewById<EditText>(R.id.inputAlarmContent)
        val startTimePicker = findViewById<TimePicker>(R.id.startTimePicker)
        val finishTimePicker = findViewById<TimePicker>(R.id.finishTimePicker)

        startTimePicker.setOnTimeChangedListener{_, hourOfDay, minute ->
            // 선택된 시간(hourOfDay)과 분(minute) 값을 사용하여 필요한 작업을 수행
            val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            Toast.makeText(this, "Selected time: $selectedTime", Toast.LENGTH_SHORT).show()
        }
        // startTimePicker의 현재 시간 가져오기
        val startHour = startTimePicker.hour
        val startMinute = startTimePicker.minute
        val startTime = LocalTime.parse(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute))

        finishTimePicker.setOnTimeChangedListener{_, hourOfDay, minute ->
            // 선택된 시간(hourOfDay)과 분(minute) 값을 사용하여 필요한 작업을 수행
            val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            Toast.makeText(this, "Selected time: $selectedTime", Toast.LENGTH_SHORT).show()
        }
        // startTimePicker의 현재 시간 가져오기
        val finishHour = finishTimePicker.hour
        val finishMinute = finishTimePicker.minute
        val finishTime = LocalTime.parse(String.format(Locale.getDefault(), "%02d:%02d", finishHour, finishMinute))

        val db = AppDatabase.getInstance(applicationContext)
        val scheduleDao = db.scheduleDao()
        val alarmDao = db.AlarmDAO()

        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedLocalDate = LocalDate.parse(selectedDate)

        binding.confirmButton.setOnClickListener {
            lifecycleScope.launch {
                val scheduleId = scheduleDao.insertSchedule(
                    Schedule(
                        title = inputAlarmTitle.text.toString(),
                        date = selectedLocalDate,
                        place = inputAlarmLocation.text.toString(),
                        start_time = startTime,
                        end_time = finishTime
                    )
                )
                alarmDao.insertAlarm(
                    Alarm(
                        scheduleId = scheduleId,
                        title = inputAlarmTitle.text.toString(),
                        message = inputAlarmContent.text.toString(),
                        time = startTime,
                        interval = 10
                    )
                )
                val title = inputAlarmTitle.text.toString()
                val content = inputAlarmContent.text.toString()
                if(title.isNotEmpty() && content.isNotEmpty()){
                    // 저장된 텍스트로 알림 발생
                    val norificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    norificationManager.notify(1, buildNotification(title, content))
                }else{
                    Toast.makeText(this@SetAlarmActivity, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this, "일정 추가 완료", Toast.LENGTH_SHORT).show()
            binding.inputAlarmTitle.setText("")
            binding.inputAlarmLocation.setText("")
            binding.inputAlarmContent.setText("")

        }
        binding.cancleButton.setOnClickListener{//닫기 버튼
            finish() // 창 닫기
        }

    }
}
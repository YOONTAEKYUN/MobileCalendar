package com.example.mobilecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.mobilecalendar.databinding.ActivityMainBinding
import com.example.mobilecalendar.roomdb.Alarm
import com.example.mobilecalendar.roomdb.AppDatabase
import com.example.mobilecalendar.roomdb.Schedule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("testt", token)
        })

        val db = AppDatabase.getInstance(applicationContext)
        val scheduleDao = db.scheduleDao()
        val alarmDao = db.AlarmDAO()
        val schedule = Schedule(title = "스케줄 제목", date = LocalDate.now(), place="서울", time = LocalTime.now())
        lifecycleScope.launch {
            val insertedId = scheduleDao.insertSchedule(schedule)
            // 삽입된 스케줄의 식별자를 사용할 수 있습니다.
        }
        lifecycleScope.launch {
            val schedules = scheduleDao.getAllSchedules()
            for (schedule in schedules) {
                Log.d("Schedule", "Title: ${schedule.title}, Date: ${schedule.date}, Time: ${schedule.time}")
            }
        }



//        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database").build()
//        Log.d("DB: ", db.toString())
//        val alarmDao = db.AlarmDAO()
//        val scheduleDao = db.scheduleDao()
//        //val schdules = scheduleDao.getAllSchedules("1")
//        val alarms = alarmDao.getAlarmForSchedule(scheduleId: Int="1")
//        for (alarm in alarms) {
//            Log.d("Database", "Alarm: $alarm")
//        }
//
//        Log.d("DB: ", alarmDao.toString())
//        val alarm = Alarm(scheduleId = 1, title = "알람1", message = "알람 메시지", time = LocalTime.now(), interval = 60)
//        lifecycleScope.launch {
//            val insertedId = alarmDao.insertAlarm(alarm)
//            // 삽입된 알람의 식별자를 사용할 수 있습니다.
//        }

    }

}
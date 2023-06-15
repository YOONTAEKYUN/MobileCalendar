package com.example.mobilecalendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecalendar.roomdb.Alarm
import com.example.mobilecalendar.roomdb.AppDatabase
import com.example.mobilecalendar.roomdb.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime

class ScheduleDetailActivity : AppCompatActivity() {
    private lateinit var schedule: Schedule
    private lateinit var alarm: Alarm

    private lateinit var inputAlarmTitle: EditText
    private lateinit var inputAlarmLocation: EditText
    private lateinit var inputAlarmContent: EditText
    private lateinit var startTimePicker: TimePicker
    private lateinit var finishTimePicker: TimePicker
    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)


        val scheduleId = intent.getLongExtra("scheduleId", -1L)


        inputAlarmTitle = findViewById(R.id.inputAlarmTitleDetail)
        inputAlarmLocation = findViewById(R.id.inputAlarmLocationDetail)
        inputAlarmContent = findViewById(R.id.inputAlarmContentDetail)
        startTimePicker = findViewById(R.id.startTimePickerDetail)
        finishTimePicker = findViewById(R.id.finishTimePickerDetail)
        cancelButton = findViewById(R.id.cancleButtonDetail)
        confirmButton = findViewById(R.id.confirmButtonDetail)


        fetchSchedule(scheduleId)


        cancelButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            val modifiedTitle = inputAlarmTitle.text.toString()
            val modifiedLocation = inputAlarmLocation.text.toString()
            val modifiedContent = inputAlarmContent.text.toString()
            val modifiedStartTime =
                LocalTime.of(startTimePicker.hour, startTimePicker.minute)
            val modifiedFinishTime =
                LocalTime.of(finishTimePicker.hour, finishTimePicker.minute)

            schedule.title = modifiedTitle
            schedule.place = modifiedLocation
            schedule.start_time = modifiedStartTime
            schedule.end_time = modifiedFinishTime

            updateSchedule(schedule, modifiedContent)

            finish()
        }
    }

    private fun fetchSchedule(scheduleId: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            val scheduleDao = AppDatabase.getInstance(applicationContext).scheduleDao()
            val alarmDao = AppDatabase.getInstance(applicationContext).AlarmDAO()
            schedule = scheduleDao.getScheduleById(scheduleId)
            alarm = alarmDao.getAlarmForSchedule(scheduleId)


            inputAlarmTitle.setText(schedule.title)
            inputAlarmLocation.setText(schedule.place)
            inputAlarmContent.setText(alarm.message)
            Log.d("Check", inputAlarmContent.toString())
            startTimePicker.hour = schedule.start_time.hour
            startTimePicker.minute = schedule.start_time.minute
            finishTimePicker.hour = schedule.end_time.hour
            finishTimePicker.minute = schedule.end_time.minute
        }
    }

    private fun updateSchedule(schedule: Schedule, modifiedContent: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val scheduleDao = AppDatabase.getInstance(applicationContext).scheduleDao()
            val alarmDao = AppDatabase.getInstance(applicationContext).AlarmDAO()
            scheduleDao.updateSchedule(schedule)
            alarmDao.updateAlarmMessage(alarm.id, modifiedContent)
        }
    }
}

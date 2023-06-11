package com.example.mobilecalendar.monthCalendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecalendar.R
import com.example.mobilecalendar.roomdb.Schedule
import java.time.format.DateTimeFormatter

class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val place: TextView = view.findViewById(R.id.place)
    val start_time: TextView = view.findViewById(R.id.start_time)
    val end_time: TextView = view.findViewById(R.id.end_time)

}

class ScheduleAdapter(private val schedules: MutableList<Schedule>) : RecyclerView.Adapter<ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.modal_item, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        // 시간 포맷 설정
        val startTimeFormatted = schedule.start_time.format(DateTimeFormatter.ofPattern("HH:mm"))
        val endTimeFormatted = schedule.end_time.format(DateTimeFormatter.ofPattern("HH:mm"))

        holder.title.text = schedule.title
        holder.place.text = schedule.place
        holder.start_time.text = startTimeFormatted
        holder.end_time.text = endTimeFormatted
    }

    override fun getItemCount() = schedules.size

    // 새로운 함수를 추가하여 일정 목록을 업데이트할 수 있도록 합니다.
    @SuppressLint("NotifyDataSetChanged")
    fun updateScheduleList(newSchedules: List<Schedule>) {
        schedules.clear()
        schedules.addAll(newSchedules)
        notifyDataSetChanged()
    }
}

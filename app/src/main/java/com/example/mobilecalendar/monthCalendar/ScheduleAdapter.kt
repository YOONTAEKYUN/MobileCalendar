package com.example.mobilecalendar.monthCalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecalendar.R
import com.example.mobilecalendar.roomdb.Schedule

class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val place: TextView = view.findViewById(R.id.place)
    val time: TextView = view.findViewById(R.id.time)
}

class ScheduleAdapter(private val schedules: MutableList<Schedule>) : RecyclerView.Adapter<ScheduleViewHolder>() {
    // ViewHolder 클래스와 onBindViewHolder 메서드는 그대로 사용합니다.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.modal_item, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.title.text = schedule.title
        holder.place.text = schedule.place
        holder.time.text = schedule.time.toString()
    }

    override fun getItemCount() = schedules.size

    // 새로운 함수를 추가하여 일정 목록을 업데이트할 수 있도록 합니다.
    fun updateScheduleList(newSchedules: List<Schedule>) {
        schedules.clear()
        schedules.addAll(newSchedules)
        notifyDataSetChanged()
    }
}

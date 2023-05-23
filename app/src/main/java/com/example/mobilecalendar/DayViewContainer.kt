package com.example.mobilecalendar

import android.view.View
import android.widget.TextView
import com.example.mobilecalendar.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    //val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}
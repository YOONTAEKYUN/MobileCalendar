package com.example.mobilecalendar.weekCalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.mobilecalendar.DayViewContainer
import com.example.mobilecalendar.databinding.WeekLayoutBinding
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


class WeekFrag : Fragment() {
    private lateinit var binding: WeekLayoutBinding

    private val selectedDates = mutableSetOf<LocalDate>()

    private fun dateClicked(date: LocalDate) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        // Refresh both calendar views..
        binding.weekCalendarView.notifyDateChanged(date)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WeekLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                view.setOnClickListener {
                    if (data.position == WeekDayPosition.RangeDate) {
                        dateClicked(date = data.date)
                    }
                }
            }
        }
        val daysOfWeek = daysOfWeek()
        binding.titlesContainer2.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title

            }
        val currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)

        return view
    }
}
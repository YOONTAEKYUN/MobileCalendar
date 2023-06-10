package com.example.mobilecalendar

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.mobilecalendar.databinding.MonthLayoutBinding
import com.example.mobilecalendar.shared.displayText
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class MonthViewContainer(view: View) : ViewContainer(view) {
    private val binding = MonthLayoutBinding.bind(view)
    val textView = binding.MonthText
    val legendLayout = binding.calendarView
}

class MonthFrag : Fragment() {
    private lateinit var binding: MonthLayoutBinding
    private var selectedDate: LocalDate? = null

    private fun showModal(date: LocalDate) {
        val modalLayout = LayoutInflater.from(requireContext()).inflate(R.layout.modal_layout, null)
        Log.d("layout", modalLayout.toString())
        // 모달 레이아웃에 날짜 정보 설정
        val dateTextView = modalLayout.findViewById<TextView>(R.id.dateTextView)
        dateTextView.text = date.toString()
        // 다이얼로그로 모달 레이아웃 표시
        val dialog = AlertDialog.Builder(requireContext())
            .setView(modalLayout)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MonthLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        val daysOfWeek = daysOfWeek()

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                view.setOnClickListener {
                    if (data.position == DayPosition.MonthDate) {
                        val currentSelection = selectedDate
                        if (currentSelection == data.date) {
                            // If the user clicks the same date, clear selection.
                            selectedDate = null
                            // Reload this date so the dayBinder is called
                            // and we can REMOVE the selection background.
                            binding.calendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = data.date
                            // Reload the newly selected date so the dayBinder is
                            // called and we can ADD the selection background.
                            binding.calendarView.notifyDateChanged(data.date)
                            if (currentSelection != null) {
                                // We need to also reload the previously selected
                                // date so we can REMOVE the selection background.
                                binding.calendarView.notifyDateChanged(currentSelection)
                            }
                            // 모달 표시
                            showModal(data.date)
                        }
                    }
                }
            }
        }

        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    container.textView.text = data.yearMonth.displayText(short = true)
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                            }
                    }
                }
            }
        binding.titlesContainer1.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title

            }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100) // 필요에 따라 조정하세요.
        val endMonth = currentMonth.plusMonths(100) // 필요에 따라 조정하세요.
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)


        return view
    }
}


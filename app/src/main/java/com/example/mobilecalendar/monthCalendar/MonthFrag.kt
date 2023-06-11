package com.example.mobilecalendar.monthCalendar

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecalendar.DayViewContainer
import com.example.mobilecalendar.R
import com.example.mobilecalendar.ScheduleDetailActivity
import com.example.mobilecalendar.SetAlarmActivity
import com.example.mobilecalendar.databinding.MonthLayoutBinding
import com.example.mobilecalendar.roomdb.AppDatabase
import com.example.mobilecalendar.roomdb.Schedule
import com.example.mobilecalendar.shared.displayText
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.ListTemplate
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
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
    private lateinit var scheduleAdapter: ScheduleAdapter

    suspend fun retrieveDefaultList(date: LocalDate): ListTemplate? {
        Log.d("date", date.toString())
        val db = AppDatabase.getInstance(requireContext())
        val scheduleDao = db.scheduleDao()
        val schedules = withContext(Dispatchers.IO) {
            //scheduleDao.getAllSchedules() //현재는 모든 스케줄을 가져오도록 설정했지만 해당 날짜에 따라 가져오도록 변경해야함
            scheduleDao.getSchedulesByDate(date)
        }
        //주의 : content 내용은 최대 3개 표시 가능, 최소 2개 이상 있어야 함
        val contents = schedules.map { schedule ->
            Content(
                title = schedule.title,
                description = schedule.date.toString(),
                imageUrl = "https://mud-kage.kakao.com/dn/bDPMIb/btqgeoTRQvd/49BuF1gNo6UXkdbKecx600/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            )
        }
        if(contents.size < 2){
            return null
        }
        // 로그로 contents 내용 출력
        contents.forEachIndexed { index, content ->
            println("Content ${index + 1}:")
            println("Title: ${content.title}")
            println("Description: ${content.description}")
            //println("ImageUrl: ${content.imageUrl}")
            //println("Link: ${content.link}")
            println()
        }
        return ListTemplate(
            headerTitle = "스케줄 리스트",
            headerLink = Link(
                webUrl = "https://developers.kakao.com",
                mobileWebUrl = "https://developers.kakao.com"
            ),
            contents = contents,
            buttons = listOf(
                com.kakao.sdk.template.model.Button(
                    "웹으로 보기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                com.kakao.sdk.template.model.Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )
    }

    private fun showModal(date: LocalDate) {
        val modalLayout = LayoutInflater.from(requireContext()).inflate(R.layout.modal_layout, null)
        val start = date.atStartOfDay().toLocalDate()
        val end = date.atTime(LocalTime.MAX).toLocalDate()
        // EditText 초기화
        val editText = modalLayout.findViewById<EditText>(R.id.context_editText)
        editText.visibility = View.VISIBLE

        // 모달 레이아웃에 날짜 정보 설정
        val dateTextView = modalLayout.findViewById<TextView>(R.id.dateTextView)
        dateTextView.text = date.toString()

        // "+ 버튼" 초기화
        val addButton = modalLayout.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            // "+ 버튼"을 클릭하면 새로운 화면이 뜨도록 처리
            // TODO: 새로운 화면을 띄우고 EditText가 나오도록
            val intent = Intent(requireContext(), SetAlarmActivity::class.java)
            intent.putExtra("selectedDate", date.toString()) // 선택된 날짜를 인텐트에 추가
            startActivity(intent)
        }
        // 일정 공유 버튼 눌렀을 때
        val shareButton = modalLayout.findViewById<Button>(R.id.share_button)
        lifecycleScope.launch {
            val defaultList = retrieveDefaultList(date)
            shareButton.setOnClickListener {
                if (defaultList != null){ //일정이 없거나 2개보다 적으면 공유 불가
                    // 카카오톡 설치여부 확인
                    if (ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())) {
                        // 카카오톡으로 카카오톡 공유 가능
                        defaultList?.let { it1 ->
                            ShareClient.instance.shareDefault(requireContext(), it1) { sharingResult, error ->
                                if (error != null) {
                                    Log.e("kakao", "카카오톡 공유 실패", error)
                                } else if (sharingResult != null) {
                                    Log.d("kakao", "카카오톡 공유 성공 ${sharingResult.intent}")
                                    startActivity(sharingResult.intent)

                                    // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                                    Log.w("kakao", "Warning Msg: ${sharingResult.warningMsg}")
                                    Log.w("kakao", "Argument Msg: ${sharingResult.argumentMsg}")
                                }
                            }
                        }
                    } else {
                        // 카카오톡 미설치: 웹 공유 사용 권장
                        // 웹 공유 예시 코드
                        val sharerUrl = defaultList?.let { it1 ->
                            WebSharerClient.instance.makeDefaultUrl(
                                it1
                            )
                        }
                        // CustomTabs으로 웹 브라우저 열기
                        // 1. CustomTabsServiceConnection 지원 브라우저 열기
                        // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
                        try {
                            if (sharerUrl != null) {
                                KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
                            }
                        } catch(e: UnsupportedOperationException) {
                            //CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
                        }

                        // 2. CustomTabsServiceConnection 미지원 브라우저 열기
                        // ex) 다음, 네이버 등
                        try {
                            if (sharerUrl != null) {
                                KakaoCustomTabsClient.open(requireContext(), sharerUrl)
                            }
                        } catch (e: ActivityNotFoundException) {
                            // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
                            Log.d("kakao", "인터넷 브라우저가 없음")
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), "일정이 최솟값 보다 작아 조회하지 못합니다.", Toast.LENGTH_LONG).show()
                }

            }
        }

        val scheduleRecyclerView = modalLayout.findViewById<RecyclerView>(R.id.scheduleRecyclerView)

        // ScheduleDAO를 사용하여 해당 날짜의 일정 데이터를 가져옵니다.
        val scheduleDAO = AppDatabase.getInstance(requireContext()).scheduleDao()
        var scheduleList: List<Schedule> = emptyList()
        runBlocking {
            scheduleList = scheduleDAO.getSchedulesByDate(start, end)
        }

        if (!::scheduleAdapter.isInitialized) {
            scheduleAdapter = ScheduleAdapter(scheduleList.toMutableList())
            scheduleRecyclerView.adapter = scheduleAdapter
        } else {
            scheduleAdapter.updateScheduleList(scheduleList)
            scheduleRecyclerView.adapter = scheduleAdapter
        }

        scheduleAdapter.setOnItemClickListener(object : ScheduleAdapter.OnItemClickListner {
            override fun onItemClick(schedule: Schedule) {
                // 여기서 아이템 클릭 이벤트를 처리합니다.
                // 예를 들어, 스케줄의 상세 보기를 위한 새로운 액티비티를 시작할 수 있습니다.
                val intent = Intent(requireContext(), ScheduleDetailActivity::class.java)
                intent.putExtra("scheduleId", schedule.id) // 스케줄 id를 상세 보기 액티비티로 전달합니다.
                startActivity(intent)
            }
        })

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
                //날짜 클릭 시 이벤트 리스너 설정
                container.textView.setOnClickListener {
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
                // 클릭 이벤트 리스너
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


package com.example.mobilecalendar

import android.content.ActivityNotFoundException
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
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.ListTemplate

// import com.kakao.sdk.template
//디버그 키 해시: yzXmrPCP5gi9Nt0gJOplguL51Sc=


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
            Log.d("token : ", token)
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

        //카카오톡 메시지 공유 api, list 템플릿
        val defaultList = ListTemplate(
            headerTitle = "WEEKLY MAGAZINE",
            headerLink = Link(
                webUrl = "https://developers.kakao.com",
                mobileWebUrl = "https://developers.kakao.com"
            ),
            contents = listOf(
                Content(
                    title = "취미의 특징, 탁구",
                    description = "스포츠",
                    imageUrl = "https://mud-kage.kakao.com/dn/bDPMIb/btqgeoTRQvd/49BuF1gNo6UXkdbKecx600/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Content(
                    title = "크림으로 이해하는 커피이야기",
                    description = "음식",
                    imageUrl = "https://mud-kage.kakao.com/dn/QPeNt/btqgeSfSsCR/0QJIRuWTtkg4cYc57n8H80/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Content(
                    title = "감성이 가득한 분위기",
                    description = "사진",
                    imageUrl = "https://mud-kage.kakao.com/dn/c7MBX4/btqgeRgWhBy/ZMLnndJFAqyUAnqu4sQHS0/kakaolink40_original.png",
                    link = Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                )
            ),
            buttons = listOf(
                Button(
                    "웹으로 보기",
                    Link(
                        webUrl = "https://developers.kakao.com",
                        mobileWebUrl = "https://developers.kakao.com"
                    )
                ),
                Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )

        // 피드 메시지 보내기

        // 카카오톡 설치여부 확인
        if (ShareClient.instance.isKakaoTalkSharingAvailable(this)) {
            // 카카오톡으로 카카오톡 공유 가능
            ShareClient.instance.shareDefault(this, defaultList) { sharingResult, error ->
                if (error != null) {
                    Log.e("TAG", "카카오톡 공유 실패", error)
                }
                else if (sharingResult != null) {
                    Log.d("TAG", "카카오톡 공유 성공 ${sharingResult.intent}")
                    startActivity(sharingResult.intent)

                    // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w("TAG", "Warning Msg: ${sharingResult.warningMsg}")
                    Log.w("TAG", "Argument Msg: ${sharingResult.argumentMsg}")
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultList)

            // CustomTabs으로 웹 브라우저 열기

            // 1. CustomTabsServiceConnection 지원 브라우저 열기
            // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
            try {
                KakaoCustomTabsClient.openWithDefault(this, sharerUrl)
            } catch(e: UnsupportedOperationException) {
                // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
            }

            // 2. CustomTabsServiceConnection 미지원 브라우저 열기
            // ex) 다음, 네이버 등
            try {
                KakaoCustomTabsClient.open(this, sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
            }
        }
    }
//    private fun sendKakaoLink() {
//        val defaultFeed = FeedTemplate(
//            content = Content(
//                title = "제목",
//                description = Title,
//                imageUrl = "https://mud-kage.kakao.com/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
//                link = Link(
//                    androidExecutionParams = mapOf("type" to "6", "route" to "main", "data" to "data")
//                )
//                // 콘텐츠를 클릭했을 때
//            ),
//            buttons = listOf(
//                Button(
//                    "자세히 보기",
//                    Link(
//                        androidExecutionParams = mapOf("type" to "6", "route" to "main", "data" to "data")
//                    )
//                )
//                //버튼을 클릭했을 때
//            )
//        )
//        if (ShareClient.instance.isKakaoTalkSharingAvailable(requireActivity())) {
//            // 카카오톡으로 카카오톡 공유 가능
//            ShareClient.instance.shareDefault(requireActivity(), defaultFeed) { sharingResult, error ->
//                if (error != null) {
//                    Timber.e( "카카오톡 공유 실패", error)
//                }
//                else if (sharingResult != null) {
//                    Timber.d( "카카오톡 공유 성공 ${sharingResult.intent}")
//                    startActivity(sharingResult.intent)
//
//                    // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
//                    Timber.w( "Warning Msg: ${sharingResult.warningMsg}")
//                    Timber.w("Argument Msg: ${sharingResult.argumentMsg}")
//                }
//            }
//        } else {
//            // 카카오톡 미설치: 웹 공유 사용 권장
//            // 웹 공유 예시 코드
//            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultFeed)
//
//            // CustomTabs으로 웹 브라우저 열기
//
//            // 1. CustomTabsServiceConnection 지원 브라우저 열기
//            // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
//            try {
//                KakaoCustomTabsClient.openWithDefault(requireActivity(), sharerUrl)
//            } catch(e: UnsupportedOperationException) {
//                // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
//            }
//
//            // 2. CustomTabsServiceConnection 미지원 브라우저 열기
//            // ex) 다음, 네이버 등
//            try {
//                KakaoCustomTabsClient.open(requireActivity(), sharerUrl)
//            } catch (e: ActivityNotFoundException) {
//                // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
//            }
//        }
//    }
}



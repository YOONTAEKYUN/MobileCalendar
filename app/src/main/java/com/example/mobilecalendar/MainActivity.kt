package com.example.mobilecalendar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.mobilecalendar.databinding.ActivityMainBinding
import com.example.mobilecalendar.roomdb.AppDatabase
import com.example.mobilecalendar.roomdb.Schedule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.ListTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 디버그 키 해시 : yzXmrPCP5gi9Nt0gJOplguL51Sc=
// 릴리즈 키 해시 : nZfccoI3KzCvTHynSMOxiSTBwXE=

import com.example.mobilecalendar.monthCalendar.MonthFrag
import com.example.mobilecalendar.weekCalendar.WeekFrag


class MainActivity : AppCompatActivity() {
    suspend fun retrieveDefaultList(): ListTemplate {
        val db = AppDatabase.getInstance(applicationContext)
        val scheduleDao = db.scheduleDao()
        val schedules = withContext(Dispatchers.IO) {
            scheduleDao.getAllSchedules()
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
    }


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


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //알림을 보내는 코드 내용
        val title = "알림"
        val content = "내용"
        val norificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        norificationManager.notify(2, buildNotification(title, content))



        val db = AppDatabase.getInstance(applicationContext)
        val scheduleDao = db.scheduleDao()
        val alarmDao = db.AlarmDAO()


        binding.month.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_view, MonthFrag())
                .commit()
        }
        binding.week.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_view, WeekFrag())
                .commit()
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MainActivity", "Firebase Token: $token")
                // 토큰을 서버로 전송하거나 필요한 곳에 사용합니다.
            } else {
                Log.e("MainActivity", "Failed to get Firebase token")
            }
        })


        binding.sendButton.setOnClickListener {
            lifecycleScope.launch {
                scheduleDao.insertSchedule(
                    Schedule(
                        title = binding.title.text.toString(),
                        date = LocalDate.now(),
                        place = binding.place.text.toString(),
                        time = LocalTime.now()
                    )
                )
            }
            Toast.makeText(this, "일정 추가 완료", Toast.LENGTH_SHORT).show()
            binding.title.setText("")
            binding.place.setText("")
        }

        lifecycleScope.launch {
            val defaultList = retrieveDefaultList()
            binding.testButton.setOnClickListener {
                // 카카오톡 설치여부 확인
                if (ShareClient.instance.isKakaoTalkSharingAvailable(this@MainActivity)) {

                    // 카카오톡으로 카카오톡 공유 가능
                    defaultList?.let { it1 ->
                        ShareClient.instance.shareDefault(this@MainActivity, it1) { sharingResult, error ->
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
                            KakaoCustomTabsClient.openWithDefault(this@MainActivity, sharerUrl)
                        }
                    } catch(e: UnsupportedOperationException) {
                        //CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
                    }

                    // 2. CustomTabsServiceConnection 미지원 브라우저 열기
                    // ex) 다음, 네이버 등
                    try {
                        if (sharerUrl != null) {
                            KakaoCustomTabsClient.open(this@MainActivity, sharerUrl)
                        }
                    } catch (e: ActivityNotFoundException) {
                        // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
                        Log.d("kakao", "인터넷 브라우저가 없음")
                    }
                }
            }

        }


//        // 카카오 api 사용을 위한 릴리즈 해시키 확인
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("hash", keyHash.toString())

    }
}



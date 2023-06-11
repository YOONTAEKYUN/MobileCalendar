package com.example.mobilecalendar

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Date


class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("MyFcmService", "New token :: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // TOKEN 값을 서버에 저장
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("MyFcmService", "Notification Title :: ${remoteMessage.notification?.title}")
        Log.d("MyFcmService", "Notification Body :: ${remoteMessage.notification?.body}")
        Log.d("MyFcmService", "Notification Data :: ${remoteMessage.data}")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // 알림 보여주기
        showNotification(title, body)

        // 조건 확인 로직을 추가하고 푸시 알림을 생성하여 보냄
//        if (/* 조건을 만족하는지 확인하는 로직 */) {
//            val notification = remoteMessage.notification
//            if (notification != null) {
//                showNotification(notification)
//            }
//        }
    }

    fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "1234"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH) //알림을 화면 상단에 배너처럼 띄움
            .setSmallIcon(R.drawable.sym_def_app_icon) // 작은 아이콘 추가
            .setContentTitle("알림 제목")
            .setContentText("알림 내용")
            .setContentIntent(pIntent)
            //.setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
        // 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Date().time.toInt(), notificationBuilder.build())

//        getSystemService(NotificationManager::class.java).run {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
//                createNotificationChannel(channel)
//            }
//
//            notify(Date().time.toInt(), notificationBuilder.build())
//        }
    }
}
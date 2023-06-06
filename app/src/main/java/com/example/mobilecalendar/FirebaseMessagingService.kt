package com.example.mobilecalendar

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
        // TODO :: TOKEN 값을 서버에 저장하자!
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO :: 전달받은 리모트 메시지를 처리하자!

        Log.d("MyFcmService", "Notification Title :: ${remoteMessage.notification?.title}")
        Log.d("MyFcmService", "Notification Body :: ${remoteMessage.notification?.body}")
        Log.d("MyFcmService", "Notification Data :: ${remoteMessage.data}")
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "1234"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH) //알림을 화면 상단에 배너처럼 띄움
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setContentIntent(pIntent)

        getSystemService(NotificationManager::class.java).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
                createNotificationChannel(channel)
            }

            notify(Date().time.toInt(), notificationBuilder.build())
        }
    }
}
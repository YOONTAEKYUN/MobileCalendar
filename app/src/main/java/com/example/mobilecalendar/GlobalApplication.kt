package com.example.mobilecalendar

import android.app.Application
import com.kakao.sdk.common.KakaoSdk;

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들
        // Kakao SDK 초기화
        KakaoSdk.init(this, "d8367dfc6b6fca4bf3dfb2b5ee5fcc4c")
    }
}
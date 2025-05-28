package com.example.project_hk2_24_25_laptrinhmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    // Application được tự động setup bởi Hilt
    // Không cần override onCreate() trừ khi có logic khởi tạo đặc biệt
}
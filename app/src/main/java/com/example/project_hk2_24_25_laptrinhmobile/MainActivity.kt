package com.example.project_hk2_24_25_laptrinhmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Đảm bảo dùng Material 3
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.project_hk2_24_25_laptrinhmobile.ui.theme.Project_HK2_24_25_LaptrinhMobileTheme // Đảm bảo tên Theme của bạn đúng
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint // Quan trọng cho Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Áp dụng theme của ứng dụng (đã được định nghĩa trong ui.theme)
            Project_HK2_24_25_LaptrinhMobileTheme { // THAY THẾ BẰNG TÊN THEME CỦA BẠN
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Màu nền từ theme
                ) {
                    // Gọi AppNavigation để thiết lập các màn hình và điều hướng
                    AppNavigation()
                }
            }
        }
    }
}
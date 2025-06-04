package com.example.project_hk2_24_25_laptrinhmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.project_hk2_24_25_laptrinhmobile.ui.theme.Project_HK2_24_25_LaptrinhMobileTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Project_HK2_24_25_LaptrinhMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Gọi AppNavigation để thiết lập các màn hình và điều hướng
                    AppNavigation()
                }
            }
        }
    }
}
package com.example.project_hk2_24_25_laptrinhmobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_hk2_24_25_laptrinhmobile.ui.screens.AboutScreen
import com.example.project_hk2_24_25_laptrinhmobile.ui.screens.DefinitionScreen
import com.example.project_hk2_24_25_laptrinhmobile.ui.screens.SearchScreen
import com.example.project_hk2_24_25_laptrinhmobile.utils.Constants
import com.example.project_hk2_24_25_laptrinhmobile.viewmodel.DictionaryViewModel

/**
 * Composable chính quản lý việc điều hướng trong ứng dụng.
 * Thiết lập NavHost và các destinations (màn hình).
 *
 * @param modifier Modifier để áp dụng cho NavHost.
 * @param navController NavController để quản lý stack điều hướng. Mặc định sẽ tạo một NavController mới.
 * @param startDestination Route của màn hình bắt đầu. Mặc định là SearchScreen.
 * @param sharedViewModel ViewModel được chia sẻ giữa các màn hình (nếu cần, nhưng ở đây mỗi màn hình có thể lấy ViewModel riêng qua Hilt).
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SEARCH_SCREEN
    // sharedViewModel: DictionaryViewModel = hiltViewModel() // ViewModel có thể được inject trực tiếp vào từng screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Màn hình Tìm kiếm
        composable(route = Routes.SEARCH_SCREEN) {
            // ViewModel sẽ được Hilt cung cấp trực tiếp cho SearchScreen
            SearchScreen(navController = navController)
        }

        // Màn hình Chi tiết Định nghĩa
        composable(
            route = Routes.DEFINITION_SCREEN_ROUTE,
            arguments = listOf(navArgument(Constants.NAV_ARG_WORD) {
                type = NavType.StringType
                nullable = false // Từ không được null để hiển thị chi tiết
            })
        ) { backStackEntry ->
            val wordArgument = backStackEntry.arguments?.getString(Constants.NAV_ARG_WORD)
            // ViewModel sẽ được Hilt cung cấp trực tiếp cho DefinitionScreen
            DefinitionScreen(
                navController = navController,
                word = wordArgument // Truyền từ lấy từ argument
            )
        }

        // Màn hình Giới thiệu
        composable(route = Routes.ABOUT_SCREEN) {
            AboutScreen(navController = navController)
        }

        // Thêm các màn hình khác ở đây (nếu có)
        // composable(route = Routes.SETTINGS_SCREEN) {
        //     SettingsScreen(navController = navController)
        // }
        // composable(route = Routes.HISTORY_SCREEN) {
        //     HistoryScreen(navController = navController)
        // }
    }
}
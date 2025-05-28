package com.example.project_hk2_24_25_laptrinhmobile


import com.example.project_hk2_24_25_laptrinhmobile.utils.Constants

/**
 * Object chứa các định danh (routes) cho các màn hình trong ứng dụng.
 * Giúp quản lý navigation một cách nhất quán và tránh lỗi hardcode string.
 */
object Routes {
    /**
     * Màn hình tìm kiếm từ điển chính.
     */
    const val SEARCH_SCREEN = "search_screen"

    /**
     * Màn hình hiển thị chi tiết định nghĩa của một từ.
     * Route này yêu cầu một argument là 'word'.
     * Ví dụ sử dụng: DEFINITION_SCREEN_ROUTE + "/{actual_word}"
     */
    const val DEFINITION_SCREEN_BASE = "definition_screen"
    const val DEFINITION_SCREEN_ROUTE = "$DEFINITION_SCREEN_BASE/{${Constants.NAV_ARG_WORD}}" // Sử dụng key từ Constants

    /**
     * Màn hình giới thiệu về ứng dụng.
     */
    const val ABOUT_SCREEN = "about_screen"

    // Bạn có thể thêm các màn hình khác ở đây nếu cần trong tương lai
    // const val SETTINGS_SCREEN = "settings_screen"
    // const val HISTORY_SCREEN = "history_screen"

    /**
     * Hàm tiện ích để tạo route đầy đủ cho màn hình chi tiết từ.
     * @param word Từ cần hiển thị chi tiết.
     * @return String route đầy đủ.
     */
    fun definitionScreenWithWord(word: String): String {
        // Nên encode URL nếu từ có thể chứa ký tự đặc biệt,
        // nhưng NavController thường xử lý tốt các ký tự cơ bản.
        // Nếu gặp vấn đề, bạn có thể dùng:
        // import java.net.URLEncoder
        // val encodedWord = URLEncoder.encode(word, "UTF-8")
        // return "$DEFINITION_SCREEN_BASE/$encodedWord"
        return "$DEFINITION_SCREEN_BASE/$word"
    }
}
package com.example.project_hk2_24_25_laptrinhmobile.utils


object Constants {
    // API Constants (Mặc dù BASE_URL đã có trong ApiService, có thể bạn muốn thêm các hằng số khác)
    // const val API_BASE_URL = "https://api.dictionaryapi.dev/" // Đã có trong DictionaryApiService
    const val DEFAULT_LANGUAGE = "en" // Ngôn ngữ mặc định

    // Navigation Arguments Keys (Dùng để lấy argument từ NavController)
    const val NAV_ARG_WORD = "word"

    // Preferences Keys (Nếu bạn sử dụng SharedPreferences)
    const val PREF_NAME = "dictionary_app_prefs"
    const val PREF_KEY_THEME = "app_theme" // Ví dụ: "light", "dark", "system"
    const val PREF_KEY_LAST_SEARCH_QUERY = "last_search_query"
    const val PREF_KEY_SEARCH_HISTORY_ENABLED = "search_history_enabled"

    // Database Constants (Nếu bạn sử dụng Room)
    const val DATABASE_NAME = "dictionary_database"
    const val WORD_INFO_TABLE_NAME = "word_info_table"
    const val SEARCH_HISTORY_TABLE_NAME = "search_history_table"

    // UI Constants
    const val DEFAULT_ANIMATION_DURATION = 300 // ms
    const val MAX_HISTORY_ITEMS_DISPLAYED = 10
    const val MAX_SUGGESTION_ITEMS_DISPLAYED = 5

    // Error Messages (Một số thông điệp lỗi chung, mặc dù repository đã có getErrorMessage)
    const val ERROR_MSG_NETWORK = "Không có kết nối mạng. Vui lòng kiểm tra lại."
    const val ERROR_MSG_UNEXPECTED = "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại."
    const val ERROR_MSG_WORD_NOT_FOUND = "Không tìm thấy từ này trong từ điển."

    // Tags for Logging
    const val TAG_APP = "DictionaryApp"
    const val TAG_API = "DictionaryAPI"
    const val TAG_DB = "DictionaryDB"
    const val TAG_UI = "DictionaryUI"
}
package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho thông tin license
 * Thường đi kèm với phonetic hoặc sourceUrls
 */
data class License(
    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
) {
    /**
     * Kiểm tra xem license có hợp lệ không
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && url.isNotBlank()
    }

    /**
     * Lấy short name của license
     */
    fun getShortName(): String {
        return when {
            name.contains("Creative Commons", ignoreCase = true) -> "CC"
            name.contains("MIT", ignoreCase = true) -> "MIT"
            name.contains("GPL", ignoreCase = true) -> "GPL"
            else -> name.take(10) // Take first 10 characters
        }
    }
}
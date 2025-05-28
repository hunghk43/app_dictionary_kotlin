package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho thông tin phonetic (phiên âm)
 * Bao gồm text và audio URL
 */
data class Phonetic(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("audio")
    val audio: String? = null,

    @SerializedName("sourceUrl")
    val sourceUrl: String? = null,

    @SerializedName("license")
    val license: License? = null
) {
    /**
     * Kiểm tra xem phonetic có hợp lệ không
     */
    fun isValid(): Boolean {
        return !text.isNullOrBlank() || !audio.isNullOrBlank()
    }

    /**
     * Kiểm tra xem có audio không
     */
    fun hasAudio(): Boolean {
        return !audio.isNullOrBlank() && audio.startsWith("http")
    }

    /**
     * Lấy text phonetic với format chuẩn
     */
    fun getFormattedText(): String {
        return if (!text.isNullOrBlank()) {
            if (text.startsWith("/") && text.endsWith("/")) {
                text
            } else {
                "/$text/"
            }
        } else {
            ""
        }
    }
}
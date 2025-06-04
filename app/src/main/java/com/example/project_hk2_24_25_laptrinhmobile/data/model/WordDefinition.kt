
package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName

data class WordDefinition(
    @SerializedName("word")
    val word: String,

    @SerializedName("phonetic")
    val phonetic: String? = null, // Trường phonetic chính từ API

    @SerializedName("phonetics")
    val phonetics: List<Phonetic> = emptyList(), // Danh sách các đối tượng Phonetic

    @SerializedName("meanings")
    val meanings: List<Meaning> = emptyList(),

    @SerializedName("license")
    val license: License? = null,

    @SerializedName("sourceUrls")
    val sourceUrls: List<String> = emptyList()
) {
    /**
     * Lấy phonetic text thô đầu tiên có sẵn.
     */
    fun getFirstPhonetic(): String? { // Hàm này trả về text thô
        return phonetic ?: phonetics.firstOrNull { !it.text.isNullOrBlank() }?.text
    }

    /**
     * Lấy audio URL đầu tiên có sẵn.
     */
    fun getFirstAudioUrl(): String? {
        return phonetics.firstOrNull { it.hasAudio() }?.audio // Giả sử Phonetic có hàm hasAudio()
    }

    /**
     * Kiểm tra xem có pronunciation audio không.
     */
    fun hasAudio(): Boolean {
        return phonetics.any { it.hasAudio() } // Giả sử Phonetic có hàm hasAudio()
    }

    /**
     * Lấy phonetic text đầu tiên có sẵn VÀ ĐÃ ĐƯỢC ĐỊNH DẠNG
     * Hàm này sẽ được gọi từ UI.
     */
    fun getFirstPhoneticText(): String? {
        // Ưu tiên lấy từ danh sách phonetics có trường 'text' không rỗng và đã được định dạng
        val formattedFromList = phonetics.firstOrNull { !it.text.isNullOrBlank() }?.getFormattedText()

        if (!formattedFromList.isNullOrBlank()) { // Kiểm tra isNullOrBlank vì getFormattedText() có thể trả về ""
            return formattedFromList
        }

        // Nếu không có từ list hoặc list trả về rỗng, thử lấy từ trường 'phonetic' chính và định dạng nó
        return phonetic?.let { ph ->
            if (ph.isBlank()) null // Trả về null nếu phonetic chính rỗng
            // Chuẩn hóa (thêm dấu / ở đầu và cuối nếu chưa có)
            else if (ph.startsWith("/") && ph.endsWith("/")) ph
            else "/${ph.trim('/')}/" // trim('/') để tránh //text//
        }

    }
}
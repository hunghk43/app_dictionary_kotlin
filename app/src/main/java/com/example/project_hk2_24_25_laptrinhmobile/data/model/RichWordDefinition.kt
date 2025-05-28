package com.example.project_hk2_24_25_laptrinhmobile.data.model


// Quan trọng: Đảm bảo EnglishWordDefinition đã được đổi tên và import đúng
// import com.example.project_hk2_24_25_laptrinhmobile.data.model.EnglishWordDefinition

data class RichWordDefinition(
    val englishDetails: WordDefinition, // Thông tin chi tiết từ dictionaryapi.dev
    var vietnameseMainTranslation: String? = null, // Nghĩa tiếng Việt chính từ MyMemory
    // Có thể thêm một list các bản dịch gợi ý khác từ MyMemory nếu muốn
    var otherVietnameseTranslations: List<String>? = null,
    // Map để lưu các bản dịch chi tiết theo yêu cầu (key: originalText, value: translatedText)
    // Khởi tạo là emptyMap để đảm bảo immutability ban đầu, sẽ tạo mutable copy khi cần cập nhật
    var detailedTranslations: Map<String, String> = emptyMap()
) {
    // Hàm tiện ích để thêm hoặc cập nhật một bản dịch chi tiết
    fun updateDetailedTranslation(original: String, translated: String): RichWordDefinition {
        val newMap = this.detailedTranslations.toMutableMap()
        newMap[original] = translated
        return this.copy(detailedTranslations = newMap)
    }

    // Hàm tiện ích để lấy bản dịch chi tiết
    fun getDetailedTranslation(original: String): String? {
        return this.detailedTranslations[original]
    }
}
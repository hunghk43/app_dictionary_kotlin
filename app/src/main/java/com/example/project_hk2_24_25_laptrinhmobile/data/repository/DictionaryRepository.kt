
package com.example.project_hk2_24_25_laptrinhmobile.data.repository

import com.example.project_hk2_24_25_laptrinhmobile.data.model.WordDefinition // Model chi tiết tiếng Anh
import com.example.project_hk2_24_25_laptrinhmobile.data.model.RichWordDefinition // Model kết hợp mới

import com.example.project_hk2_24_25_laptrinhmobile.data.remote.ApiResult
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.EnglishDictionaryApiService
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.RetrofitClient
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.TranslationApiService
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.safeApiCall

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(
    private val englishApiService: EnglishDictionaryApiService, // Inject service tiếng Anh
    private val translationApiService: TranslationApiService  // Inject service dịch
) {

    private val richWordCache = mutableMapOf<String, RichWordDefinition>()

    private val searchHistory = mutableListOf<String>()

    /**
     * Lấy chi tiết từ điển tiếng Anh và nghĩa tiếng Việt chính.
     * @param englishWord Từ tiếng Anh cần tra cứu.
     * @return Flow<ApiResult<RichWordDefinition>>
     */
    fun getWordDetailsWithTranslation(englishWord: String): Flow<ApiResult<RichWordDefinition>> = flow {
        emit(ApiResult.Loading)
        val cleanWord = englishWord.trim().lowercase() // Chuẩn hóa từ tìm kiếm

        // 1. Validate input
        if (cleanWord.isBlank()) {
            emit(ApiResult.Error("Vui lòng nhập từ để tra cứu."))
            return@flow
        }
        // Thêm các validation khác nếu cần (ví dụ: độ dài từ)

        // 2. Kiểm tra cache
        richWordCache[cleanWord]?.let { cachedRichWord ->
            emit(ApiResult.Success(cachedRichWord))
            // addToSearchHistory(cleanWord) // Cân nhắc có nên cập nhật history khi lấy từ cache không
            return@flow
        }

        try {
            // 3. Gọi API lấy chi tiết tiếng Anh (dictionaryapi.dev)
            val englishDetailsResponse = englishApiService.getEnglishWordDetail(cleanWord)

            if (!englishDetailsResponse.isSuccessful || englishDetailsResponse.body().isNullOrEmpty()) {
                val errorMsg = if (!englishDetailsResponse.isSuccessful) {
                    RetrofitClient.getErrorMessage(HttpException(englishDetailsResponse))
                } else {
                    "Không tìm thấy định nghĩa tiếng Anh cho \"$englishWord\"."
                }
                emit(ApiResult.Error(errorMsg))
                return@flow
            }

            // Lấy thông tin tiếng Anh (thường là phần tử đầu tiên trong danh sách)
            val englishDefinition: WordDefinition = englishDetailsResponse.body()!!.first()

            // 4. Gọi API dịch sang tiếng Việt (MyMemory) cho từ gốc
            var vietnameseMainTranslation: String? = null
            var otherVietnameseTranslations: List<String>? = null

            try { // Đặt try-catch riêng cho API dịch để nếu nó lỗi, ta vẫn có thể trả về phần tiếng Anh
                val translationApiResponse = translationApiService.getMyMemoryTranslation(textToTranslate = englishDefinition.word) // Dịch từ gốc
                if (translationApiResponse.isSuccessful && translationApiResponse.body() != null) {
                    val myMemoryData = translationApiResponse.body()!!
                    vietnameseMainTranslation = myMemoryData.responseData?.translatedText?.takeIf { it.isNotBlank() }
                        ?: myMemoryData.matches
                            ?.filter { !it.translation.isNullOrBlank() }
                            ?.maxByOrNull { (it.quality?.toFloatOrNull() ?: 0f) * (it.matchNumeric ?: 0f) }
                            ?.translation
                    // Lấy thêm các bản dịch khác nếu muốn
                    otherVietnameseTranslations = myMemoryData.matches
                        ?.mapNotNull { it.translation }
                        ?.distinct()
                        ?.filter { it.isNotBlank() && it != vietnameseMainTranslation } // Loại bỏ bản dịch chính nếu trùng
                        ?.take(2) // Ví dụ lấy thêm 2 gợi ý
                } else {
                    // Log lỗi dịch nhưng không làm crash flow chính, người dùng vẫn nhận được chi tiết tiếng Anh

                    System.err.println("Lỗi dịch MyMemory: ${if (!translationApiResponse.isSuccessful) HttpException(translationApiResponse).message else "Response body null"}")
                }
            } catch (translationException: Exception) {
                // Log lỗi dịch
                System.err.println("Ngoại lệ khi dịch MyMemory: ${translationException.message}")
            }

            // 5. Tạo đối tượng RichWordDefinition và cache lại
            val richWordResult = RichWordDefinition(
                englishDetails = englishDefinition,
                vietnameseMainTranslation = vietnameseMainTranslation,
                otherVietnameseTranslations = otherVietnameseTranslations
                // detailedTranslations sẽ được cập nhật sau nếu có tính năng dịch chi tiết
            )
            richWordCache[cleanWord] = richWordResult
            addToSearchHistory(cleanWord) // Thêm vào lịch sử sau khi có kết quả (kể cả khi dịch lỗi)

            emit(ApiResult.Success(richWordResult))

        } catch (e: Exception) { // Lỗi từ API tiếng Anh hoặc lỗi không lường trước
            emit(ApiResult.Error(RetrofitClient.getErrorMessage(e)))
        }
    }.flowOn(Dispatchers.IO) // Thực hiện tất cả trên IO thread

    /**
     * Dịch một đoạn văn bản cụ thể từ Anh sang Việt sử dụng MyMemory API.
     * Dùng cho tính năng dịch chi tiết theo yêu cầu.
     * @param textToTranslate Đoạn văn bản tiếng Anh cần dịch.
     * @return ApiResult<String> chứa bản dịch tiếng Việt hoặc lỗi.
     */
    suspend fun translateSpecificText(textToTranslate: String): ApiResult<String> = withContext(Dispatchers.IO) {
        if (textToTranslate.isBlank()) {
            return@withContext ApiResult.Error("Không có nội dung để dịch.")
        }
        try {
            val response = translationApiService.getMyMemoryTranslation(textToTranslate = textToTranslate)
            if (response.isSuccessful && response.body() != null) {
                val myMemoryData = response.body()!!
                val translatedText = myMemoryData.responseData?.translatedText?.takeIf { it.isNotBlank() }
                    ?: myMemoryData.matches
                        ?.filter { !it.translation.isNullOrBlank() }
                        ?.maxByOrNull { (it.quality?.toFloatOrNull() ?: 0f) * (it.matchNumeric ?: 0f) }
                        ?.translation

                if (translatedText != null && translatedText.isNotBlank()) {
                    ApiResult.Success(translatedText)
                } else {
                    ApiResult.Error("Không tìm thấy bản dịch.")
                }
            } else {
                ApiResult.Error(RetrofitClient.getErrorMessage(HttpException(response)))
            }
        } catch (e: Exception) {
            ApiResult.Error(RetrofitClient.getErrorMessage(e))
        }
    }

    private fun addToSearchHistory(word: String) {
        val cleanWord = word.trim().lowercase()
        searchHistory.remove(cleanWord) // Xóa nếu đã tồn tại để đưa lên đầu
        searchHistory.add(0, cleanWord)
        // Giới hạn kích thước lịch sử
        if (searchHistory.size > 50) { // Có thể dùng Constants.MAX_HISTORY_SIZE
            searchHistory.removeAt(searchHistory.size - 1)
        }
    }

    fun getSearchHistory(): List<String> {
        return searchHistory.toList() // Trả về bản copy để tránh sửa đổi từ bên ngoài
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistory.clear()
        richWordCache.clear() // Khi xóa lịch sử, cũng nên cân nhắc xóa cache liên quan
    }

    // Các hàm khác như validateWord, getSearchSuggestions có thể giữ nguyên
    // hoặc được điều chỉnh để làm việc với từ tiếng Anh đầu vào.
    fun validateWord(word: String): ValidationResult { // Giữ nguyên từ trước
        val cleanWord = word.trim()
        return when {
            cleanWord.isBlank() -> ValidationResult.Error("Vui lòng nhập từ")
            cleanWord.length < 1 -> ValidationResult.Error("Từ phải có ít nhất 1 ký tự") // MyMemory có thể dịch 1 chữ
            cleanWord.length > 100 -> ValidationResult.Error("Từ quá dài")
            !cleanWord.matches(Regex("^[a-zA-Z0-9\\s'-]+$")) -> // Cho phép số nếu từ có thể chứa số
                ValidationResult.Error("Từ chứa ký tự không hợp lệ")
            else -> ValidationResult.Valid
        }
    }

    fun getSearchSuggestions(query: String): List<String> {
        return if (query.isBlank()) {
            searchHistory.take(5)
        } else {
            searchHistory.filter {
                it.startsWith(query.trim().lowercase(), ignoreCase = true)
            }.take(5)
        }
    }
}

// Các data class/sealed class phụ trợ (ValidationResult, RepositoryStats nếu có) có thể giữ nguyên
// ValidationResult đã được  định nghĩa ở file trước
sealed class ValidationResult { // Đảm bảo nó tồn tại
    object Valid : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
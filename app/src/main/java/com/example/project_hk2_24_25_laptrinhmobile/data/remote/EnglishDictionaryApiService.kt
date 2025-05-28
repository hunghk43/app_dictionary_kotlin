package com.example.project_hk2_24_25_laptrinhmobile.data.remote



import com.example.project_hk2_24_25_laptrinhmobile.data.model.WordDefinition // Đảm bảo import này đúng sau khi đổi tên model
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EnglishDictionaryApiService {
    /**
     * Lấy thông tin chi tiết của một từ tiếng Anh từ dictionaryapi.dev.
     * @param word Từ tiếng Anh cần tra cứu.
     * @return Response chứa danh sách EnglishWordDefinition (thường chỉ có một phần tử).
     */
    @GET("api/v2/entries/en/{word}")
    suspend fun getEnglishWordDetail(@Path("word") word: String): Response<List<WordDefinition>>
}

object EnglishApiConstants {
    const val BASE_URL = "https://api.dictionaryapi.dev/"
}
package com.example.project_hk2_24_25_laptrinhmobile.data.remote


import com.example.project_hk2_24_25_laptrinhmobile.data.model.MyMemoryResponse // Import model cho MyMemory API
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApiService {
    /**
     * Dịch văn bản từ tiếng Anh sang tiếng Việt sử dụng MyMemory API.
     * @param textToTranslate Văn bản tiếng Anh cần dịch.
     * @param langPair Cặp ngôn ngữ, mặc định là "en|vi".
     * @param email (Tùy chọn) Email của bạn để có thể nhận được giới hạn request cao hơn hoặc thông báo.
     * @return Response chứa MyMemoryResponse.
     */
    @GET("get") // Endpoint của MyMemory API là "/get"
    suspend fun getMyMemoryTranslation(
        @Query("q") textToTranslate: String,
        @Query("langpair") langPair: String = "en|vi",
        @Query("de") email: String? = null
    ): Response<MyMemoryResponse>
}

object MyMemoryApiConstants {
    const val BASE_URL = "https://api.mymemory.translated.net/"
}
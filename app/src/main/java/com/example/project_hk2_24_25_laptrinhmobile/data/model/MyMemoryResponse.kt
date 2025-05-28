// data/model/MyMemoryResponse.kt
package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName

data class MyMemoryResponse(
    @SerializedName("responseData")
    val responseData: ResponseData?,

    @SerializedName("responseDetails")
    val responseDetails: String?, // Thêm trường này nếu API có trả về

    @SerializedName("responseStatus")
    val responseStatus: Int?,

    @SerializedName("matches")
    val matches: List<MatchItem>?
    // Thêm các trường khác ở cấp cao nhất của JSON response nếu có
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String?,

    @SerializedName("match")
    val match: Float? // Độ khớp của bản dịch chính
)

data class MatchItem(
    @SerializedName("id")
    val id: String?,

    @SerializedName("segment") // Từ/cụm từ gốc
    val segment: String?,

    @SerializedName("translation") // Bản dịch của segment này
    val translation: String?,

    @SerializedName("source")
    val sourceLanguage: String?, // Ngôn ngữ nguồn

    @SerializedName("target")
    val targetLanguage: String?, // Ngôn ngữ đích

    @SerializedName("quality")
    val quality: String?, // Chất lượng, thường là string "0"-"100"

    @SerializedName("reference")
    val reference: String?, // Nguồn tham khảo

    @SerializedName("usage-count")
    val usageCount: Int?,

    @SerializedName("created-by")
    val createdBy: String?,

    @SerializedName("last-updated-by")
    val lastUpdatedBy: String?,

    @SerializedName("create-date")
    val createDate: String?,

    @SerializedName("last-update-date")
    val lastUpdateDate: String?,

    @SerializedName("match") // API MyMemory có trường "match" trong "matches"
    val matchNumeric: Float? // Đặt tên khác để tránh xung đột nếu cần
)
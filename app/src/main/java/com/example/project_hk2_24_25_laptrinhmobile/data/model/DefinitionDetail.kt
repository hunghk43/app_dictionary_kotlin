package com.example.project_hk2_24_25_laptrinhmobile.data.model
import com.google.gson.annotations.SerializedName

data class DefinitionDetail(
    @SerializedName("definition")
    val definition: String?, // Định nghĩa bằng tiếng Anh

    @SerializedName("example")
    val example: String? = null, // Ví dụ bằng tiếng Anh

    @SerializedName("synonyms")
    val synonyms: List<String>? = emptyList(),

    @SerializedName("antonyms")
    val antonyms: List<String>? = emptyList()
    // Thêm các trường khác nếu API dictionaryapi.dev cung cấp cho mỗi "definition"
)
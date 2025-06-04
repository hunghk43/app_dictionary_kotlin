
package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName


data class Meaning(
    @SerializedName("partOfSpeech")
    val partOfSpeech: String,

    @SerializedName("definitions")
    val definitions: List<DefinitionDetail> = emptyList(),
    @SerializedName("synonyms")
    val synonyms: List<String> = emptyList(),

    @SerializedName("antonyms")
    val antonyms: List<String> = emptyList()
) {
    // Các hàm tiện ích có thể cần điều chỉnh nếu tên trường trong DefinitionDetail khác
    fun getFirstDefinition(): String? {
        // Giả sử DefinitionDetail có trường 'definition'
        return definitions.firstOrNull()?.definition
    }

    fun getFirstExample(): String? {
        // Giả sử DefinitionDetail có trường 'example'
        return definitions.firstOrNull { it.example?.isNotBlank() == true }?.example
    }

    /**
     * Kiểm tra xem có synonyms không
     */
    fun hasSynonyms(): Boolean {
        return synonyms.isNotEmpty()
    }

    /**
     * Kiểm tra xem có antonyms không
     */
    fun hasAntonyms(): Boolean {
        return antonyms.isNotEmpty()
    }

    /**
     * Format part of speech để hiển thị
     */
    fun getFormattedPartOfSpeech(): String {
        return partOfSpeech.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}
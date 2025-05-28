package com.example.project_hk2_24_25_laptrinhmobile.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho một định nghĩa cụ thể
 * Bao gồm definition text và example
 */
data class Definition(
    @SerializedName("definition")
    val definition: String,

    @SerializedName("synonyms")
    val synonyms: List<String> = emptyList(),

    @SerializedName("antonyms")
    val antonyms: List<String> = emptyList(),

    @SerializedName("example")
    val example: String? = null
) {
    /**
     * Kiểm tra xem có example không
     */
    fun hasExample(): Boolean {
        return !example.isNullOrBlank()
    }

    /**
     * Kiểm tra xem có synonyms không
     */
    fun hasSynonyms(): Boolean {
        return synonyms.isNotEmpty()
    }

    /**
     * Kiểm tra xử có antonyms không
     */
    fun hasAntonyms(): Boolean {
        return antonyms.isNotEmpty()
    }

    /**
     * Format definition để hiển thị (capitalize first letter)
     */
    fun getFormattedDefinition(): String {
        return definition.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    /**
     * Format example để hiển thị với quotes
     */
    fun getFormattedExample(): String? {
        return example?.let { "\"$it\"" }
    }
}
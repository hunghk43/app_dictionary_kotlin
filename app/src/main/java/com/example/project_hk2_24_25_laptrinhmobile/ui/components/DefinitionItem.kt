
package com.example.project_hk2_24_25_laptrinhmobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_hk2_24_25_laptrinhmobile.data.model.DefinitionDetail
import com.example.project_hk2_24_25_laptrinhmobile.data.model.Meaning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinitionItem(
    meaning: Meaning,
    detailedTranslations: Map<String, String>,
    onTranslateRequest: (textToTranslate: String) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(meaning.definitions.size <= 1) }

    Card(
        modifier = modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Hiển thị Loại từ (Part of Speech)
            meaning.partOfSpeech?.let { partOfSpeech ->
                Text(
                    text = partOfSpeech.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Hiển thị danh sách các định nghĩa (DefinitionDetail)
            val definitionsToDisplay = if (expanded || meaning.definitions.size <= 1) {
                meaning.definitions
            } else {
                meaning.definitions.take(1) // Chỉ hiển thị 1 định nghĩa đầu tiên nếu chưa mở rộng và có nhiều hơn 1
            }

            definitionsToDisplay.forEachIndexed { index, definitionDetail ->
                // Lấy bản dịch đã có từ map (nếu có)
                val translatedDefinition = detailedTranslations[definitionDetail.definition]
                val translatedExample = detailedTranslations[definitionDetail.example]

                DefinitionDetailEntryView( // Đổi tên thành một Composable con rõ ràng hơn
                    definitionDetail = definitionDetail,
                    index = index + 1, // Số thứ tự
                    translatedDefinition = translatedDefinition,
                    translatedExample = translatedExample,
                    onTranslateRequest = onTranslateRequest
                )
                // Thêm Divider giữa các DefinitionDetail nếu không phải là cái cuối cùng trong danh sách hiển thị
                if (index < definitionsToDisplay.size - 1) {
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                }
            }

            // Nút "Xem thêm" / "Thu gọn" nếu có nhiều hơn 1 định nghĩa
            if (meaning.definitions.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (expanded) "Thu gọn" else "Xem thêm ${meaning.definitions.size - 1} định nghĩa")
                }
            }
        }
    }
}

@Composable
private fun DefinitionDetailEntryView(
    definitionDetail: DefinitionDetail,
    index: Int,
    translatedDefinition: String?,
    translatedExample: String?,
    onTranslateRequest: (textToTranslate: String) -> Unit
) {
    Column {
        // 1. Phần Định nghĩa (Definition)
        definitionDetail.definition?.takeIf { it.isNotBlank() }?.let { engDefinition ->
            TextWithTranslationOption(
                originalText = "$index. $engDefinition",
                translatedText = translatedDefinition?.let { "TV: $it" },
                textStyle = MaterialTheme.typography.bodyLarge,
                translationStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary
                ),
                onTranslateRequest = { onTranslateRequest(engDefinition) }
            )
        }

        // 2. Phần Ví dụ (Example)
        definitionDetail.example?.takeIf { it.isNotBlank() }?.let { engExample ->
            Spacer(modifier = Modifier.height(6.dp)) //
            TextWithTranslationOption(
                originalText = "e.g., \"$engExample\"",
                translatedText = translatedExample?.let { "TV: \"$it\"" },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                ),
                translationStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                ),
                onTranslateRequest = { onTranslateRequest(engExample) },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Composable con để hiển thị một đoạn văn bản gốc và bản dịch của nó (nếu có),
 * cùng với nút để yêu cầu dịch.
 */
@Composable
private fun TextWithTranslationOption(
    originalText: String,
    translatedText: String?,
    textStyle: androidx.compose.ui.text.TextStyle,
    translationStyle: androidx.compose.ui.text.TextStyle,
    onTranslateRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = originalText,
                style = textStyle,
                modifier = Modifier.weight(1f)
            )

            if (translatedText.isNullOrBlank()) {
                IconButton(
                    onClick = onTranslateRequest,
                    modifier = Modifier.size(36.dp).padding(start = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.Translate,
                        contentDescription = "Dịch",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Hiển thị bản dịch nếu có với hiệu ứng xuất hiện/biến mất
        AnimatedVisibility(
            visible = !translatedText.isNullOrBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Text(
                text = translatedText!!,
                style = translationStyle,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
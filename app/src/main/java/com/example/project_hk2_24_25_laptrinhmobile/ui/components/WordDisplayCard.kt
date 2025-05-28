package com.example.project_hk2_24_25_laptrinhmobile.ui.components

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_hk2_24_25_laptrinhmobile.data.model.RichWordDefinition
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDisplayCard(
    richWordDefinition: RichWordDefinition,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val englishDetails = richWordDefinition.englishDetails
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun playAudio(url: String?) {
        if (url.isNullOrBlank()) return
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
                // Optional: Add error listener for MediaPlayer
                setOnErrorListener { _, _, _ ->
                    // Handle error, e.g., show a toast or log
                    true
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Từ tiếng Anh và nút phát âm
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = englishDetails.word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                englishDetails.getFirstAudioUrl()?.let { audioUrl ->
                    IconButton(onClick = { playAudio(audioUrl) }) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Phát âm")
                    }
                }
            }

            // Phiên âm tiếng Anh
            englishDetails.getFirstPhonetic()?.let { phonetic -> // SỬA Ở ĐÂY: getFirstPhonetic()
                if (phonetic.isNotBlank()) {
                    // Logic chuẩn hóa phonetic (thêm dấu /) nên nằm trong hàm getFirstPhonetic()
                    // hoặc bạn phải tự làm ở đây.
                    // Ví dụ, nếu getFirstPhonetic() chỉ trả về text thô:
                    val formattedPhonetic = if (phonetic.startsWith("/") && phonetic.endsWith("/")) phonetic else "/$phonetic/"
                    Text(
                        text = formattedPhonetic, // Sử dụng phonetic đã chuẩn hóa
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Nghĩa tiếng Việt chính
            richWordDefinition.vietnameseMainTranslation?.let { translation ->
                if (translation.isNotBlank()) {
                    Text(
                        text = "Tiếng Việt: $translation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Hiển thị một vài nghĩa tiếng Anh đầu tiên làm preview (tùy chọn)
            englishDetails.meanings.firstOrNull()?.let { firstMeaning ->
                Text(
                    text = "(${firstMeaning.partOfSpeech?.lowercase()}) ${firstMeaning.definitions.firstOrNull()?.definition}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
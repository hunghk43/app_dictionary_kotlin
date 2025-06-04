package com.example.project_hk2_24_25_laptrinhmobile.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_hk2_24_25_laptrinhmobile.data.model.WordDefinition
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.ApiResult
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.DefinitionItem
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.ErrorView
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.LoadingIndicator
import com.example.project_hk2_24_25_laptrinhmobile.viewmodel.DictionaryViewModel
import com.example.project_hk2_24_25_laptrinhmobile.viewmodel.UiEvent
import kotlinx.coroutines.launch
import com.example.project_hk2_24_25_laptrinhmobile.data.model.RichWordDefinition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinitionScreen(
    navController: NavController,
    word: String?, // Từ tiếng Anh được truyền qua route
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    // selectedRichWord bây giờ là StateFlow<RichWordDefinition?>
    val selectedRichWordState by viewModel.selectedRichWord.collectAsState()

    // Xử lý UI Event (ví dụ Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel
                    )
                }
                // Thêm else branch để handle các trường hợp khác
                else -> {
                    // Có thể để trống hoặc log các event khác
                }
            }
        }
    }

    // Fetch chi tiết nếu selectedRichWord là null hoặc không khớp với word từ argument
    LaunchedEffect(word, selectedRichWordState) {
        if (word != null && (selectedRichWordState == null || selectedRichWordState?.englishDetails?.word?.equals(word, ignoreCase = true) == false)) {
            // Gọi hàm searchWord để đảm bảo cả chi tiết Anh và dịch Việt chính được fetch
            // Hoặc tạo một hàm riêng trong ViewModel để fetch chỉ cho DefinitionScreen
            viewModel.searchWord(word)
        }
    }

    val currentRichDef = selectedRichWordState // Hoặc lấy từ searchResult nếu logic  là vậy

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(currentRichDef?.englishDetails?.word?.replaceFirstChar { it.uppercaseChar() } ?: word ?: "Chi tiết")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {

            viewModel.searchResult.collectAsState().value is ApiResult.Loading && currentRichDef == null -> {
                LoadingIndicator(Modifier.fillMaxSize().padding(paddingValues))
            }
            currentRichDef != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WordHeaderDetailed(richWordDefinition = currentRichDef)
                    }

                    // Hiển thị các meaning tiếng Anh
                    currentRichDef.englishDetails.meanings.forEachIndexed { index, meaning ->
                        item(key = meaning.partOfSpeech + index) {
                            DefinitionItem(
                                meaning = meaning,
                                detailedTranslations = currentRichDef.detailedTranslations,
                                onTranslateRequest = { textToTranslate ->

                                    viewModel.translateDetailedText(
                                        originalText = textToTranslate,
                                        wordContext = currentRichDef.englishDetails.word
                                    )
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    // Hiển thị sourceUrls nếu có
                    if (currentRichDef.englishDetails.sourceUrls.isNotEmpty()) {
                        item {
                            SourceUrlsSectionView(sourceUrls = currentRichDef.englishDetails.sourceUrls)
                        }
                    }
                }
            }
            // Xử lý lỗi nếu searchResult là Error và currentRichDef vẫn null
            viewModel.searchResult.collectAsState().value is ApiResult.Error && currentRichDef == null -> {
                val errorResult = viewModel.searchResult.collectAsState().value as ApiResult.Error
                ErrorView(
                    errorMessage = errorResult.message,
                    onRetry = { word?.let { viewModel.searchWord(it) } },
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }
            else -> { // Trường hợp word là null hoặc không tìm thấy dữ liệu
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có dữ liệu để hiển thị.")
                }
            }
        }
    }
}

@Composable
private fun WordHeaderDetailed(richWordDefinition: RichWordDefinition) {

    val englishDetails = richWordDefinition.englishDetails

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = englishDetails.word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            // Nút phát âm (tương tự WordDisplayCard)
            englishDetails.getFirstAudioUrl()?.let { audioUrl ->
                IconButton(
                    onClick = {
                        // TODO: Implement audio playback
                    }
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Phát âm"
                    )
                }
            }
        }

        englishDetails.getFirstPhoneticText()?.let { phonetic ->
            if (phonetic.isNotBlank()) {
                Text(
                    phonetic,
                    style = MaterialTheme.typography.titleSmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        richWordDefinition.vietnameseMainTranslation?.let { translation ->
            if (translation.isNotBlank()) {
                Text(
                    text = "Tiếng Việt: $translation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Hiển thị các bản dịch gợi ý khác nếu có
        richWordDefinition.otherVietnameseTranslations?.let { others ->
            if (others.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gợi ý khác: ${others.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun SourceUrlsSectionView(sourceUrls: List<String>) {

    Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Text(
            "Nguồn tham khảo (từ điển Anh-Anh):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        sourceUrls.forEach { url ->
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable {
                    // TODO: Mở URL trong trình duyệt
                }
            )
        }
    }
}
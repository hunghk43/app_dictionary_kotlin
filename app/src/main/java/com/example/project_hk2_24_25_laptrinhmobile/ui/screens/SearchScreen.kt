package com.example.project_hk2_24_25_laptrinhmobile.ui.screens


import com.example.project_hk2_24_25_laptrinhmobile.viewmodel.UiEvent
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_hk2_24_25_laptrinhmobile.Routes
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.ApiResult
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.ErrorView
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.LoadingIndicator
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.SearchBar
import com.example.project_hk2_24_25_laptrinhmobile.ui.components.WordDisplayCard
import com.example.project_hk2_24_25_laptrinhmobile.viewmodel.DictionaryViewModel
import kotlinx.coroutines.launch
import com.example.project_hk2_24_25_laptrinhmobile.data.model.RichWordDefinition // Import model mới

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    // searchResult bây giờ là ApiResult<RichWordDefinition>?
    val searchResult by viewModel.searchResult.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    // ... (snackbarHostState, coroutineScope, context, mediaPlayer - có thể bỏ mediaPlayer ở đây nếu WordDisplayCard tự quản lý)

    // Xử lý UI Event (ví dụ Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Từ điển Anh - Việt") }, // Cập nhật title
                actions = {
                    IconButton(onClick = { navController.navigate(route = Routes.ABOUT_SCREEN) }) {
                        Icon(Icons.Default.Info, contentDescription = "Giới thiệu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onSearch = { viewModel.searchWord(it) }, // searchWord không cần tham số nữa nếu lấy từ searchQuery
                isLoading = searchResult is ApiResult.Loading,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Hiển thị kết quả hoặc trạng thái khác
            val currentResult = searchResult
            when (currentResult) {
                is ApiResult.Loading -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                is ApiResult.Success -> {
                    // API dictionaryapi.dev thường trả về list 1 phần tử cho 1 từ đơn
                    // Hàm repository của chúng ta cũng trả về 1 RichWordDefinition
                    val richWordDef = currentResult.data
                    Column(modifier = Modifier.fillMaxSize()) { // Sử dụng Column thay vì LazyColumn nếu chỉ có 1 item
                        WordDisplayCard(
                            richWordDefinition = richWordDef,
                            onCardClick = {
                                viewModel.selectRichWordDefinition(richWordDef) // Chọn từ này
                                navController.navigate(route = Routes.definitionScreenWithWord(richWordDef.englishDetails.word))
                            }
                        )
                        // Nếu repository trả về List<RichWordDefinition>, thì dùng LazyColumn ở đây
                    }

                }
                is ApiResult.Error -> {
                    ErrorView(
                        errorMessage = currentResult.message,
                        onRetry = { viewModel.searchWord() }, // Gọi searchWord không tham số
                        modifier = Modifier.fillMaxSize()
                    )
                }
                null -> { // Trạng thái ban đầu hoặc sau khi clear
                    // ... (logic hiển thị search history giữ nguyên)
                    if (searchHistory.isNotEmpty() && searchQuery.isBlank()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Lịch sử tìm kiếm:",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(searchHistory.take(10)) { historyItem ->
                                    Text(
                                        text = historyItem,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { // Đảm bảo clickable được import
                                                viewModel.onSearchQueryChanged(historyItem)
                                                viewModel.searchWord(historyItem)
                                            }
                                            .padding(vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Divider()
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nhập từ tiếng Anh cần tra.", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
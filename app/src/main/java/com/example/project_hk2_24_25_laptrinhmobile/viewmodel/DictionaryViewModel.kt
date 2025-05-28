// viewmodel/DictionaryViewModel.kt
package com.example.project_hk2_24_25_laptrinhmobile.viewmodel
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_hk2_24_25_laptrinhmobile.data.model.RichWordDefinition // Import model mới
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.ApiResult
import com.example.project_hk2_24_25_laptrinhmobile.data.repository.DictionaryRepository
// Import extension function nếu nó nằm ở file khác (ví dụ, nếu searchWithValidation không còn dùng)
// import com.example.project_hk2_24_25_laptrinhmobile.data.repository.searchWithValidation // Có thể không cần nữa
import com.example.project_hk2_24_25_laptrinhmobile.utils.Constants
import com.example.project_hk2_24_25_laptrinhmobile.utils.NetworkConnectivityObserver
import com.example.project_hk2_24_25_laptrinhmobile.utils.NetworkStatus
import com.example.project_hk2_24_25_laptrinhmobile.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiEvent { // Giữ nguyên UiEvent
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent()
}

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    application: Application, // Hilt tự động cung cấp Application cho AndroidViewModel
    private val repository: DictionaryRepository,
    networkObserver: NetworkConnectivityObserver // Hilt tự động cung cấp từ AppModule
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow cho kết quả tìm kiếm, giờ là ApiResult<RichWordDefinition>
    // Vì SearchScreen có thể hiển thị một danh sách (mặc dù API từ điển Anh-Anh thường trả về 1 item cho 1 từ),
    // hoặc chỉ một kết quả chính. Hiện tại, hàm repository trả về 1 RichWordDefinition.
    private val _searchResult = MutableStateFlow<ApiResult<RichWordDefinition>?>(null)
    val searchResult: StateFlow<ApiResult<RichWordDefinition>?> = _searchResult.asStateFlow()

    // StateFlow để lưu RichWordDefinition đang được chọn (ví dụ khi vào DefinitionScreen)
    private val _selectedRichWord = MutableStateFlow<RichWordDefinition?>(null)
    val selectedRichWord: StateFlow<RichWordDefinition?> = _selectedRichWord.asStateFlow()

    // Các StateFlow khác giữ nguyên
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    val networkStatus: StateFlow<NetworkStatus> = networkObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = if (application.applicationContext.isNetworkAvailable()) NetworkStatus.Available else NetworkStatus.Unavailable
        )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    init {
        loadSearchHistory()
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() { // Logic giữ nguyên
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status != NetworkStatus.Available) {
                    if (_searchResult.value is ApiResult.Loading) {
                        _searchResult.value = ApiResult.Error(Constants.ERROR_MSG_NETWORK)
                    }
                }
            }
        }
    }

    private fun loadSearchHistory() { // Logic giữ nguyên
        viewModelScope.launch {
            _searchHistory.value = repository.getSearchHistory()
            if (_searchQuery.value.isBlank()) {
                _searchSuggestions.value = _searchHistory.value.take(Constants.MAX_SUGGESTION_ITEMS_DISPLAYED)
            }
        }
    }

    fun onSearchQueryChanged(query: String) { // Logic giữ nguyên
        _searchQuery.value = query
        suggestionJob?.cancel()
        if (query.isBlank()) {
            _searchResult.value = null
            _selectedRichWord.value = null // Cập nhật
            _searchSuggestions.value = _searchHistory.value.take(Constants.MAX_SUGGESTION_ITEMS_DISPLAYED)
            return
        }
        suggestionJob = viewModelScope.launch {
            delay(300L)
            _searchSuggestions.value = repository.getSearchSuggestions(query)
                .take(Constants.MAX_SUGGESTION_ITEMS_DISPLAYED)
        }
    }

    /**
     * Tìm kiếm từ, lấy chi tiết tiếng Anh và nghĩa tiếng Việt chính.
     */
    fun searchWord(word: String? = null) {
        val queryToSearch = (word ?: _searchQuery.value).trim()
        searchJob?.cancel()

        if (queryToSearch.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar("Vui lòng nhập từ để tìm kiếm.")) }
            return
        }

        if (networkStatus.value != NetworkStatus.Available) {
            _searchResult.value = ApiResult.Error(Constants.ERROR_MSG_NETWORK)
            _selectedRichWord.value = null // Đặt null khi lỗi mạng
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar(Constants.ERROR_MSG_NETWORK)) }
            return
        }

        if (word != null) _searchQuery.value = queryToSearch
        _searchSuggestions.value = emptyList()

        searchJob = viewModelScope.launch {
            repository.getWordDetailsWithTranslation(queryToSearch).collect { result ->
                _searchResult.value = result
                when (result) {
                    is ApiResult.Success -> {
                        _selectedRichWord.value = result.data
                        loadSearchHistory()
                    }
                    is ApiResult.Error -> {
                        _selectedRichWord.value = null // Đặt null khi lỗi
                        viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar(result.message)) }
                    }
                    is ApiResult.Loading -> Unit
                }
            }
        }
    }
    /**
     * Chọn một RichWordDefinition (ví dụ: khi người dùng click vào item trong SearchScreen
     * để xem chi tiết trong DefinitionScreen).
     */
    fun selectRichWordDefinition(richDefinition: RichWordDefinition?) {
        _selectedRichWord.value = richDefinition // Dòng này phải được thực thi
        Log.d("ViewModelDebug", "Selected rich word: ${richDefinition?.englishDetails?.word}") // Thêm log
    }

    /**
     * Dịch một đoạn văn bản cụ thể (định nghĩa hoặc ví dụ).
     * @param originalText Đoạn văn bản tiếng Anh cần dịch.
     * @param wordContext Từ gốc tiếng Anh (để xác định RichWordDefinition cần cập nhật).
     */
    fun translateDetailedText(originalText: String, wordContext: String) {
        val currentRichDef = _selectedRichWord.value // Hoặc lấy từ _searchResult nếu UI đang hiển thị từ đó
        if (currentRichDef == null || currentRichDef.englishDetails.word.lowercase() != wordContext.lowercase() || originalText.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar("Không thể dịch chi tiết lúc này.")) }
            return
        }

        // Kiểm tra xem đã dịch chưa
        if (currentRichDef.detailedTranslations.containsKey(originalText)) {
            return // Đã có bản dịch, không cần gọi API lại
        }

        if (networkStatus.value != NetworkStatus.Available) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar(Constants.ERROR_MSG_NETWORK)) }
            return
        }

        viewModelScope.launch {
            // Optional: emit một trạng thái loading cho việc dịch text này nếu UI cần
            when (val translationResult = repository.translateSpecificText(originalText)) {
                is ApiResult.Success -> {
                    // Cập nhật bản đồ detailedTranslations trong RichWordDefinition
                    val updatedRichDef = currentRichDef.updateDetailedTranslation(originalText, translationResult.data)
                    _selectedRichWord.value = updatedRichDef // Cập nhật StateFlow để UI recompose

                    // Nếu _searchResult đang giữ RichWordDefinition này, cũng cần cập nhật nó
                    // Điều này quan trọng nếu SearchScreen cũng có thể hiển thị bản dịch chi tiết.
                    val currentSearchResultVal = _searchResult.value
                    if (currentSearchResultVal is ApiResult.Success && currentSearchResultVal.data.englishDetails.word == updatedRichDef.englishDetails.word) {
                        _searchResult.value = ApiResult.Success(updatedRichDef)
                    }
                }
                is ApiResult.Error -> {
                    viewModelScope.launch { _uiEvent.emit(UiEvent.ShowSnackbar("Lỗi dịch chi tiết: ${translationResult.message}")) }
                }
                is ApiResult.Loading -> { /* Optional */ }
            }
        }
    }

    fun clearSearch() { // Logic tương tự, cập nhật _selectedRichWord
        _searchQuery.value = ""
        _searchResult.value = null
        _selectedRichWord.value = null
        _searchSuggestions.value = _searchHistory.value.take(Constants.MAX_SUGGESTION_ITEMS_DISPLAYED)
        searchJob?.cancel()
        suggestionJob?.cancel()
    }

    fun clearSearchHistory() { // Logic giữ nguyên
        viewModelScope.launch {
            repository.clearSearchHistory()
            loadSearchHistory()
            _uiEvent.emit(UiEvent.ShowSnackbar("Lịch sử tìm kiếm đã được xóa."))
        }
    }

    // refreshCurrentWord có thể cần điều chỉnh để gọi lại getWordDetailsWithTranslation
    fun refreshCurrentWord() {
        val wordToRefresh = _selectedRichWord.value?.englishDetails?.word ?: _searchQuery.value
        if (wordToRefresh.isNotBlank()) {
            searchWord(wordToRefresh) // Gọi lại hàm searchWord để lấy cả chi tiết Anh và dịch Việt
        }
    }
}
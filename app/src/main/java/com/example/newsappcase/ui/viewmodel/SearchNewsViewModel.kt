package com.example.newsappcase.ui.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.domain.usecase.CachedNewsRepositoryOperationsUseCase
import com.example.newsappcase.domain.usecase.GetNewsType
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.domain.usecase.SendAuthCodeParam
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.util.Constants.Companion.SEARCH_DELAY
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase,
    private val cachedNewsRepositoryOperationsUseCase: CachedNewsRepositoryOperationsUseCase
) : CommonNewsViewModel(
    saveAndDeleteNewsUseCase,
    networkConnectionInterceptor,
    getNewsUseCase,
    cachedNewsRepositoryOperationsUseCase
) {

    companion object {
        private val TAG = "SearchNewsViewModel"
        private val SEARCH_FOR_NEWS = "Search For Articles Here!"
    }

    private val _newsSearchData: MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.NoConnection(null))
    val newsSearchData: StateFlow<Resource<NewsResponse>> = _newsSearchData

    private val _infoText = MutableStateFlow(SEARCH_FOR_NEWS)
    val infoText: StateFlow<String> = _infoText

    private val _shouldShowInfo = MutableStateFlow(true)
    val shouldShowInfo: StateFlow<Boolean> = _shouldShowInfo

    val searchTextFlow = MutableStateFlow("")
    private var searchNewsOffset = 0
    private var searchNewsLimit = 10
    private var searchNewsResponse: NewsResponse? = null
    private var currentQuery = ""

    init {
        viewModelScope.launch {
            searchTextFlow.debounce(SEARCH_DELAY).collectLatest { text ->
                if (text.isEmpty() || text.isBlank()) {
                    queryCleaned()
                    _infoText.emit(SEARCH_FOR_NEWS)
                    _shouldShowInfo.emit(true)
                } else {
                    searchNews(text)
                    _shouldShowInfo.emit(false)
                }
            }
        }
    }

    fun setInfoText(text: String) = viewModelScope.launch {
        _infoText.emit(text)
    }

    fun setShouldShowInfo(shouldShow: Boolean) = viewModelScope.launch {
        _shouldShowInfo.emit(shouldShow)
    }

    fun queryCleaned() = viewModelScope.launch {
        currentQuery = ""
        searchNewsOffset = 0
        searchNewsResponse = null
        _newsSearchData.emit(Resource.Success(null))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        val isPaginating = searchQuery == currentQuery
        currentQuery = searchQuery
        Log.d(TAG, "Search query: $searchQuery")
        if (!checkInternetConnection()) {
            updateOfflineNewsData(Resource.NoConnection(null))
            return@launch
        }
        if (!isPaginating) {
            searchNewsOffset = 0
            searchNewsResponse = null
        }
        _newsSearchData.emit(Resource.Loading())
        val param = SendAuthCodeParam(
            type = GetNewsType.SEARCH_NEWS,
            limit = searchNewsLimit,
            offset = searchNewsOffset,
            searchText = searchQuery
        )
        getNewsUseCase.invoke(param).catch {
            Log.d(TAG, "Error: ${it.message}")
            _newsSearchData.emit(Resource.Error(it.message.toString()))
        }.collect { response ->
            _newsSearchData.emit(handleSearchNewsResponse(response))
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsOffset += searchNewsLimit
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.results
                    val newArticles = resultResponse.results
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
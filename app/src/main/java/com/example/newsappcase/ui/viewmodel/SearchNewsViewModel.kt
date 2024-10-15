package com.example.newsappcase.ui.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.usecase.GetNewsType
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.domain.usecase.SendAuthCodeParam
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase,
    private val cachedNewsLocalRepository: CachedNewsLocalRepository
) : CommonNewsViewModel(
    saveAndDeleteNewsUseCase,
    networkConnectionInterceptor,
    getNewsUseCase,
    cachedNewsLocalRepository
) {

    private val TAG = "SearchNewsViewModel"
    private var searchNewsOffset = 0
    private var searchNewsLimit = 10
    private var searchNewsResponse: NewsResponse? = null

    init {
        viewModelScope.launch {
            if (checkInternetConnection()) {
                searchNews("", false)
            } else {
                _offlineNewsData.emit(Resource.NoConnection(null))
            }
        }
    }

    fun searchNews(searchQuery: String, isPaginating: Boolean) = viewModelScope.launch {
        Log.d(TAG, "Search query: $searchQuery")
        if (!checkInternetConnection()) {
            _offlineNewsData.emit(Resource.NoConnection(null))
            return@launch
        }
        if (!isPaginating) {
            searchNewsOffset = 0
            searchNewsResponse = null
        }
        newsData.postValue(Resource.Loading())
        val param = SendAuthCodeParam(
            type = GetNewsType.SEARCH_NEWS,
            limit = searchNewsLimit,
            offset = searchNewsOffset,
            searchText = searchQuery
        )
        getNewsUseCase.invoke(param).catch {
            Log.d(TAG, "Error: ${it.message}")
            newsData.postValue(Resource.Error(it.message.toString()))
        }.collect { response ->
            newsData.postValue(handleSearchNewsResponse(response))
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
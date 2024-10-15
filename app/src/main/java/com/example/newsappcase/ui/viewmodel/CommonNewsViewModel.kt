package com.example.newsappcase.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.usecase.GetNewsType
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.domain.usecase.SendAuthCodeParam
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
open class CommonNewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase,
    private val cachedNewsLocalRepository: CachedNewsLocalRepository
) : ViewModel() {
    private val TAG = "CommonNewsViewModel"

    private val _newsData: MutableStateFlow<Resource<NewsResponse>> = MutableStateFlow(Resource.NoConnection(null))
    val newsData: StateFlow<Resource<NewsResponse>> = _newsData

    private val _offlineNewsData = MutableSharedFlow<Resource<List<Article>>>()
    val offlineNewsData: SharedFlow<Resource<List<Article>>> = _offlineNewsData


    var newsOffset = 0
    private var newsLimit = 10
    var newsResponse: NewsResponse? = null

    fun updateNewsData(newData: Resource<NewsResponse>) {
        viewModelScope.launch {
            _newsData.emit(newData)
        }
    }

    fun updateOfflineNewsData(newData: Resource<List<Article>>) {
        viewModelScope.launch {
            _offlineNewsData.emit(newData)
        }
    }
    fun getNews() = viewModelScope.launch {
        if (!checkInternetConnection()) {
            return@launch
        }
        _newsData.emit(Resource.Loading())
        val param = SendAuthCodeParam(
            type = GetNewsType.SEARCH_NEWS,
            limit = newsLimit,
            offset = newsOffset
        )
        getNewsUseCase.invoke(param).catch {
            Log.d(TAG, "Error: ${it.message}")
            _newsData.emit(Resource.Error(it.message.toString()))
        }.collect { response ->
            response.body()?.results?.forEach { article ->
                if (article.id != null && cachedNewsLocalRepository.canInsertNewArticle(article.id)) {
                    cachedNewsLocalRepository.insert(article)
                }
            }
            _newsData.emit(handleGetNewsResponse(response))
        }
    }

    fun getCachedNewsForOfflineMode() = viewModelScope.launch {
        _offlineNewsData.emit(Resource.NoConnection(cachedNewsLocalRepository.getAllCachedArticles()))
    }

    private fun handleGetNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                newsOffset += newsLimit
                if (newsResponse == null) {
                    newsResponse = resultResponse
                } else {
                    val oldArticles = newsResponse?.results
                    val newArticles = resultResponse.results
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(newsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        saveAndDeleteNewsUseCase.insertNews(article)
    }


    fun deleteArticle(article: Article) = viewModelScope.launch {
        saveAndDeleteNewsUseCase.deleteNews(article)
    }

    fun checkInternetConnection(): Boolean {
        return networkConnectionInterceptor.isInternetAvailable()
    }

}
package com.example.newsappcase.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.api.NewsRemoteRepository
import com.example.newsappcase.repository.NewsLocalRepository
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val remoteRepository: NewsRemoteRepository,
    private val localRepository: NewsLocalRepository,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor
) : ViewModel() {

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsOffset = 0
    var newsLimit = 10
    var newsResponse: NewsResponse? = null

    val searchNewsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsOffset = 0
    var searchNewsLimit = 10
    var searchNewsResponse: NewsResponse? = null

    init {
        if(checkInternetConnection())  {
            getNews()
        }
    }

    fun getNews() = viewModelScope.launch {
        newsData.postValue(Resource.Loading())
        val response = remoteRepository.getNews(newsLimit, newsOffset)
        newsData.postValue(handleGetNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        Log.d("NewsViewModel", "Search query: $searchQuery")
        newsData.postValue(Resource.Loading())
        val response = remoteRepository.searchNews(searchQuery, searchNewsLimit, searchNewsOffset)
        searchNewsData.postValue(handleSearchNewsResponse(response))// YANLIS handleSearchNewsResponse
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
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        localRepository.insert(article)
    }

    fun getSavedArticles() = localRepository.getSavedArticles()

    fun deleteAllArticles() = viewModelScope.launch {
        localRepository.deleteAllArticles()
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        localRepository.deleteArticle(article)
    }

    private fun checkInternetConnection(): Boolean {
        return networkConnectionInterceptor.isInternetAvailable()
    }

}
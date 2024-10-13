package com.example.newsappcase.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.repository.NewsRepository
import com.example.newsappcase.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
val repository: NewsRepository
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
        getNews()
    }

    fun getNews() = viewModelScope.launch {
        newsData.postValue(Resource.Loading())
        val response = repository.getNews(newsLimit, newsOffset)
        newsData.postValue(handleGetNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        Log.d("NewsViewModel", "Search query: $searchQuery")
        newsData.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery, searchNewsLimit, searchNewsOffset)
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
        repository.insert(article)
    }

    fun getSavedArticles() = repository.getSavedArticles()

    fun deleteAllArticles() = viewModelScope.launch {
        repository.deleteAllArticles()
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

}
package com.example.newsappcase.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.api.NewsRemoteRepository
import com.example.newsappcase.domain.repository.NewsLocalRepository
import com.example.newsappcase.domain.usecase.GetNewsType
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.domain.usecase.SendAuthCodeParam
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsOffset = 0

    private var newsLimit = 10
    private var newsResponse: NewsResponse? = null
    private var searchNewsOffset = 0
    private var searchNewsLimit = 10
    private var searchNewsResponse: NewsResponse? = null

    init {
        if(checkInternetConnection())  {
            getNews()
        }
    }

    fun getNews() = viewModelScope.launch {
        newsData.postValue(Resource.Loading())
        val param = SendAuthCodeParam(type = GetNewsType.SEARCH_NEWS, limit =  newsLimit, offset = newsOffset)
        getNewsUseCase.invoke(param).catch {
            Log.d("NewsViewModel", "Error: ${it.message}")
            newsData.postValue(Resource.Error(it.message.toString()))
        }.collect { response ->
            newsData.postValue(handleGetNewsResponse(response))
        }
    }

    fun searchNews(searchQuery: String, isPaginating: Boolean) = viewModelScope.launch {
        Log.d("NewsViewModel", "Search query: $searchQuery")
        if(!isPaginating) {
            searchNewsOffset = 0
            searchNewsResponse = null
        }
        newsData.postValue(Resource.Loading())
        val param = SendAuthCodeParam(type = GetNewsType.SEARCH_NEWS, limit =  searchNewsLimit, offset = searchNewsOffset, searchText = searchQuery)
        getNewsUseCase.invoke(param).catch {
            Log.d("NewsViewModel", "Error: ${it.message}")
            newsData.postValue(Resource.Error(it.message.toString()))
        }.collect { response ->
            newsData.postValue(handleSearchNewsResponse(response))
        }
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
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        saveAndDeleteNewsUseCase.insertNews(article)
    }

    fun getSavedArticles() = saveAndDeleteNewsUseCase.getSavedNews()


    fun deleteArticle(article: Article) = viewModelScope.launch {
        saveAndDeleteNewsUseCase.deleteNews(article)
    }

    fun checkIsArticleSaved(article: Article) = viewModelScope.launch {
        article.id?.let {
            if (saveAndDeleteNewsUseCase.checkArticleIsSaved(it)) {
                deleteArticle(article)
            } else {
                saveArticle(article)
            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        return networkConnectionInterceptor.isInternetAvailable()
    }

}
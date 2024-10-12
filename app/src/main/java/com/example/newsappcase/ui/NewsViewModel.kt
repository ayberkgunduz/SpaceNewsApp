package com.example.newsappcase.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.repository.NewsRepository
import com.example.newsappcase.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
val repository: NewsRepository
) : ViewModel() {

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsOffset = 0
    var newsLimit = 10

    init {
        getNews()
    }

    fun getNews() = viewModelScope.launch {
        newsData.postValue(Resource.Loading())
        val response = repository.getNews(newsLimit, newsOffset)
        newsData.postValue(handleGetNewsResponse(response))
    }

    private fun handleGetNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
package com.example.newsappcase.api

import com.example.newsappcase.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

interface NewsRemoteRepository {
    suspend fun getNews(limit: Int, offset: Int): Response<NewsResponse>

    suspend fun searchNews(searchQuery: String, limit: Int, offset: Int): Response<NewsResponse>
}
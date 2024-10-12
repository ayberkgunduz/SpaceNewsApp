package com.example.newsappcase.api

import com.example.newsappcase.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v4/articles/")
    suspend fun getArticles(
    @Query("limit")
    limit: Int = 10,
    @Query("offset")
    offset: Int = 10
    ): Response<NewsResponse>

    @GET("v4/articles/")
    suspend fun searchArticles(
        @Query("limit")
        limit: Int = 10,
        @Query("offset")
        offset: Int = 10
    ): Response<NewsResponse>
}
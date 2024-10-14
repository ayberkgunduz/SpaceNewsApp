package com.example.newsappcase.api

import javax.inject.Inject

class NewsRemoteRepository @Inject constructor(
    private val newsService: NewsAPI
) {
    suspend fun getNews(limit: Int, offset: Int) =
        newsService.getArticles(limit, offset)

    suspend fun searchNews(searchQuery: String, limit: Int, offset: Int) =
        newsService.searchArticles(searchQuery, limit, offset)
}
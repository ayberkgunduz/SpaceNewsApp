package com.example.newsappcase.api

import javax.inject.Inject

class NewsRemoteRepositoryImpl @Inject constructor(
    private val newsService: NewsAPI
) : NewsRemoteRepository {
    override suspend fun getNews(limit: Int?, offset: Int?) =
        newsService.getArticles(limit, offset)

    override suspend fun searchNews(searchQuery: String?, limit: Int?, offset: Int?) =
        newsService.searchArticles(searchQuery, limit, offset)
}

package com.example.newsappcase.repository

import com.example.newsappcase.api.RetrofitInstance
import com.example.newsappcase.db.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getNews(limit: Int, offset: Int) =
        RetrofitInstance.api.getArticles(limit, offset)
}
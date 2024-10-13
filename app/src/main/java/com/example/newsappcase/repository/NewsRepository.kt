package com.example.newsappcase.repository

import com.example.newsappcase.api.RetrofitInstance
import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.model.Article

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getNews(limit: Int, offset: Int) =
        RetrofitInstance.api.getArticles(limit, offset)

    suspend fun searchNews(searchQuery: String, limit: Int, offset: Int) =
        RetrofitInstance.api.searchArticles(searchQuery, limit, offset)

    suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    fun getSavedArticles() = db.getArticleDao().getAllArticles()

    suspend fun deleteAllArticles() = db.getArticleDao().deleteAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}
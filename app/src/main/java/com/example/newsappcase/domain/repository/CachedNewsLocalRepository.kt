package com.example.newsappcase.domain.repository

import androidx.lifecycle.LiveData
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.CachedArticle

interface CachedNewsLocalRepository {
    suspend fun insert(article: Article): Long

    fun getSavedArticles(): LiveData<List<CachedArticle>>

    suspend fun deleteAllArticles()

    suspend fun deleteArticle(id: Int)

    suspend fun checkIfArticleExists(id: Int): Boolean

    suspend fun insertAll(articles: List<Article>): List<Long>

    suspend fun canInsertNewArticle(articleId: Int): Boolean

    suspend fun getAllCachedArticles(): List<Article>
}
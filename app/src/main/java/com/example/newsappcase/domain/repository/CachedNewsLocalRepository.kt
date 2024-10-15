package com.example.newsappcase.domain.repository

import androidx.lifecycle.LiveData
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.CachedArticle

interface CachedNewsLocalRepository {
    suspend fun insert(article: Article): Long

    suspend fun deleteAllArticles()

    suspend fun canInsertNewArticle(articleId: Int): Boolean

    suspend fun getAllCachedArticles(): List<Article>
}
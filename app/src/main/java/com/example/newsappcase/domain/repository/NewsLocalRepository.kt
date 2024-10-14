package com.example.newsappcase.domain.repository

import androidx.lifecycle.LiveData
import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.model.Article
import javax.inject.Inject

interface NewsLocalRepository {
    suspend fun insert(article: Article): Long

    fun getSavedArticles(): LiveData<List<Article>>

    suspend fun deleteAllArticles()

    suspend fun deleteArticle(id: Int)

    suspend fun checkIfArticleExists(id: Int): Boolean
}
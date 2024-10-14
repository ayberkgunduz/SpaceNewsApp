package com.example.newsappcase.repository

import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.model.Article
import javax.inject.Inject

class NewsLocalRepository  @Inject constructor(
    private val db: ArticleDatabase
) {
    suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    fun getSavedArticles() = db.getArticleDao().getAllArticles()

    suspend fun deleteAllArticles() = db.getArticleDao().deleteAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}
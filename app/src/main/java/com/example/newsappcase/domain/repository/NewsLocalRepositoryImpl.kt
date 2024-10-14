package com.example.newsappcase.domain.repository

import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.model.Article
import javax.inject.Inject

class NewsLocalRepositoryImpl @Inject constructor(
    private val db: ArticleDatabase
): NewsLocalRepository {
    override suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    override fun getSavedArticles() = db.getArticleDao().getAllArticles()

    override suspend fun deleteAllArticles() = db.getArticleDao().deleteAllArticles()

    override suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}
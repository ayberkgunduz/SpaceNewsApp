package com.example.newsappcase.domain.repository

import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.CachedArticle
import com.example.newsappcase.model.toArticles
import com.example.newsappcase.model.toCachedArticle
import javax.inject.Inject

class CachedNewsLocalRepositoryImpl @Inject constructor(
    private val db: ArticleDatabase
): CachedNewsLocalRepository {
    override suspend fun insert(article: Article) = db.getCachedArticleDao().insert(article.toCachedArticle())

    override fun getSavedArticles() = db.getCachedArticleDao().getAllArticles()

    override suspend fun deleteAllArticles() = db.getCachedArticleDao().deleteAllArticles()

    override suspend fun deleteArticle(id: Int) = db.getCachedArticleDao().deleteArticle(id)

    override suspend fun checkIfArticleExists(id: Int) = db.getCachedArticleDao().isArticleExists(id)

    override suspend fun insertAll(articles: List<Article>) = db.getCachedArticleDao().insertArticles(articles)

    override suspend fun canInsertNewArticle(articleId: Int) = db.getCachedArticleDao().canInsertNewArticle(articleId) && (db.getCachedArticleDao().getArticleCount() < 50)

    override suspend fun getAllCachedArticles() = db.getCachedArticleDao().getAllCachedArticles().toArticles()

}
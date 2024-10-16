package com.example.newsappcase.domain.usecase

import androidx.lifecycle.LiveData
import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.repository.NewsLocalRepository
import com.example.newsappcase.model.Article
import javax.inject.Inject

class CachedNewsRepositoryOperationsUseCase @Inject
constructor(
private val cachedNewsLocalRepository: CachedNewsLocalRepository,
) {

    suspend fun insert(article: Article) {
        cachedNewsLocalRepository.insert(article)
    }

    suspend fun getAllCachedArticles(): List<Article> {
        return cachedNewsLocalRepository.getAllCachedArticles()
    }

    suspend fun deleteAllArticles() {
        cachedNewsLocalRepository.deleteAllArticles()
    }

    suspend fun canInsertNewArticle(id: Int): Boolean {
        return cachedNewsLocalRepository.canInsertNewArticle(id)
    }
}
package com.example.newsappcase.domain.usecase

import androidx.lifecycle.LiveData
import com.example.newsappcase.domain.repository.NewsLocalRepository
import com.example.newsappcase.model.Article
import javax.inject.Inject


class SaveAndDeleteNewsUseCase @Inject
constructor(
    private val localRepository: NewsLocalRepository,
) {

    suspend fun insertNews(article: Article) {
        localRepository.insert(article)
    }

    fun getSavedNews(): LiveData<List<Article>> {
        return localRepository.getSavedArticles()
    }

    suspend fun deleteNews(article: Article) {
        article.id?.let { localRepository.deleteArticle(it) }
    }

    suspend fun checkArticleIsSaved(id: Int): Boolean {
        return localRepository.checkIfArticleExists(id)
    }
}

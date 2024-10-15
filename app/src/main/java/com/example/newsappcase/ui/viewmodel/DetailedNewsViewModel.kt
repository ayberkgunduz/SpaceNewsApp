package com.example.newsappcase.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.model.Article
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.util.FavoriteEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedNewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase,
    private val cachedNewsLocalRepository: CachedNewsLocalRepository
) : CommonNewsViewModel(
    saveAndDeleteNewsUseCase,
    networkConnectionInterceptor,
    getNewsUseCase,
    cachedNewsLocalRepository
){
    private val _favoriteEvent = MutableSharedFlow<FavoriteEvent>()
    val favoriteEvent: SharedFlow<FavoriteEvent> = _favoriteEvent

    fun checkIsArticleSaved(article: Article) = viewModelScope.launch {
        article.id?.let {
            if (saveAndDeleteNewsUseCase.checkArticleIsSaved(it)) {
                deleteArticle(article)
                _favoriteEvent.emit(FavoriteEvent.RemovedFavorite)
            } else {
                saveArticle(article)
                _favoriteEvent.emit(FavoriteEvent.AddedFavorite)
            }
        }
    }
}
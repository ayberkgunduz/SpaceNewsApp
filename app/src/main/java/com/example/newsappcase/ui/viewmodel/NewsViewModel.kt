package com.example.newsappcase.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.newsappcase.network.NetworkConnectionInterceptor
import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.usecase.CachedNewsRepositoryOperationsUseCase
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val saveAndDeleteNewsUseCase: SaveAndDeleteNewsUseCase,
    private val networkConnectionInterceptor: NetworkConnectionInterceptor,
    private val getNewsUseCase: GetNewsUseCase,
    private val cachedNewsRepositoryOperationsUseCase: CachedNewsRepositoryOperationsUseCase
) : CommonNewsViewModel(
    saveAndDeleteNewsUseCase,
    networkConnectionInterceptor,
    getNewsUseCase,
    cachedNewsRepositoryOperationsUseCase
) {
    init {
        viewModelScope.launch {
            if (checkInternetConnection()) {
                cachedNewsRepositoryOperationsUseCase.deleteAllArticles()
            }
        }
    }

    fun initNews() {
        viewModelScope.launch {
            if (checkInternetConnection()) {
                getNews()
            } else {
                getCachedNewsForOfflineMode()
            }
        }
    }
}
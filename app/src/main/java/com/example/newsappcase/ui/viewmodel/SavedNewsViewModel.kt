package com.example.newsappcase.ui.viewmodel

import com.example.newsappcase.domain.repository.CachedNewsLocalRepository
import com.example.newsappcase.domain.usecase.GetNewsUseCase
import com.example.newsappcase.domain.usecase.SaveAndDeleteNewsUseCase
import com.example.newsappcase.network.NetworkConnectionInterceptor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(
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
    fun getSavedArticles() = saveAndDeleteNewsUseCase.getSavedNews()
}
package com.example.newsappcase.util

sealed class FavoriteEvent {
    data object RemovedFavorite : FavoriteEvent()
    data object AddedFavorite : FavoriteEvent()
}
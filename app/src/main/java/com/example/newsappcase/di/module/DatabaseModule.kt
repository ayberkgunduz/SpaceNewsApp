package com.example.newsappcase.di.module

import android.app.Application
import androidx.room.Room
import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.domain.repository.NewsLocalRepository
import com.example.newsappcase.domain.repository.NewsLocalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideArticleDatabase(app: Application): ArticleDatabase {
        return Room.databaseBuilder(
            app,
            ArticleDatabase::class.java,
            "article_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(db: ArticleDatabase) = db.getArticleDao()

    @Provides
    @Singleton
    fun provideNewsLocalRepository(db: ArticleDatabase): NewsLocalRepository {
        return NewsLocalRepositoryImpl(db)
    }


}
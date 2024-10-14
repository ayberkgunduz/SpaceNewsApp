package com.example.newsappcase.di.module

import android.app.Application
import androidx.room.Room
import com.example.newsappcase.db.ArticleDatabase
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


}
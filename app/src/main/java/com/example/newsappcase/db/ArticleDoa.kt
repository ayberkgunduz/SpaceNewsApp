package com.example.newsappcase.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappcase.model.Article

@Dao
interface ArticleDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("DELETE FROM articles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: Int)

    @Query("SELECT COUNT(*) > 0 FROM articles WHERE id = :articleId")
    suspend fun isArticleExists(articleId: Int): Boolean


}
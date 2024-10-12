package com.example.newsappcase.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
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

    @Query("DELETE FROM articles")//(...) eklenmeyebilir!!
    suspend fun deleteAllArticles()

}
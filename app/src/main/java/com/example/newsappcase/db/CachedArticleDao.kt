package com.example.newsappcase.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappcase.model.Article
import com.example.newsappcase.model.CachedArticle

@Dao
interface CachedArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: CachedArticle): Long

    @Query("SELECT * FROM cachedArticles")
    suspend fun getAllCachedArticles(): List<CachedArticle>

    @Query("DELETE FROM cachedArticles")
    suspend fun deleteAllArticles()

    @Query("""
        SELECT (COUNT(*) < 50) AND 
        NOT EXISTS (SELECT 1 FROM cachedArticles WHERE id = :articleId) 
        AS result
        """)
    suspend fun canInsertNewArticle(articleId: Int): Boolean

    @Query("SELECT COUNT(*) FROM cachedArticles")
    suspend fun getArticleCount(): Int

}
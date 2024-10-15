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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>): List<Long>

    @Query("SELECT * FROM cachedArticles")
    fun getAllArticles(): LiveData<List<CachedArticle>>

    @Query("SELECT * FROM cachedArticles")
    suspend fun getAllCachedArticles(): List<CachedArticle>

    @Query("DELETE FROM cachedArticles")
    suspend fun deleteAllArticles()

    @Query("DELETE FROM cachedArticles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: Int)

    @Query("SELECT COUNT(*) > 0 FROM cachedArticles WHERE id = :articleId")
    suspend fun isArticleExists(articleId: Int): Boolean

    @Query("""
        SELECT (COUNT(*) < 50) AND 
        NOT EXISTS (SELECT 1 FROM cachedArticles WHERE id = :articleId) 
        AS result
        """)
    suspend fun canInsertNewArticle(articleId: Int): Boolean

    @Query("SELECT COUNT(*) FROM cachedArticles")
    suspend fun getArticleCount(): Int

}
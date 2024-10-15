package com.example.newsappcase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "cachedArticles"
)
data class CachedArticle(
    @PrimaryKey(autoGenerate = true)
    var idKey: Int? = null,
    val events: List<Event?>?,
    val featured: Boolean?,
    val id: Int?,
    val image_url: String?,
    val launches: List<Launche?>?,
    val news_site: String?,
    val published_at: String?,
    val summary: String?,
    val title: String?,
    val updated_at: String?,
    val url: String?
) : Serializable

fun Article.toCachedArticle(): CachedArticle {
    return CachedArticle(
        idKey = this.idKey,
        events = this.events,
        featured = this.featured,
        id = this.id,
        image_url = this.image_url,
        launches = this.launches,
        news_site = this.news_site,
        published_at = this.published_at,
        summary = this.summary,
        title = this.title,
        updated_at = this.updated_at,
        url = this.url
    )
}

fun List<Article>.toCachedArticles(): List<CachedArticle> {
    return this.map { it.toCachedArticle() }
}

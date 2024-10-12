package com.example.newsappcase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var idKey: Int? = null,
    val events: List<Event>,
    val featured: Boolean,
    val id: Int,
    val image_url: String,
    val launches: List<Launche>,
    val news_site: String,
    val published_at: String,
    val summary: String,
    val title: String,
    val updated_at: String,
    val url: String
)
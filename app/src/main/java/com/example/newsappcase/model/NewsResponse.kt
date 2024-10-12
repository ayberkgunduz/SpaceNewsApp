package com.example.newsappcase.model

data class NewsResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<Article>
)
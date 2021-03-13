package com.app.newsapp.model

data class APIData<T>(
    var status: String,
    var totalResults: String,
    var code: String,
    var message: String,
    var articles: List<T>
)
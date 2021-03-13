package com.app.newsapp.model

data class APIDataList<T>(
    var status: String,
    var sources: List<T>
)
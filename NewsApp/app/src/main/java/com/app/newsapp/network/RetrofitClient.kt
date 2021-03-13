package com.app.newsapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    var BaseURL:String?="http://newsapi.org/v2/"

    val instance: APIService by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.create(APIService::class.java)
    }
}

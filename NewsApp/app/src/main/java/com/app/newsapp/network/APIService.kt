package com.app.newsapp.network

import com.app.newsapp.model.APIData
import com.app.newsapp.model.APIDataList
import com.app.newsapp.model.Articles
import com.app.newsapp.model.Source
import com.app.newsapp.network.RetrofitClient.BaseURL
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIService {

    companion object {
        operator fun invoke(): APIService {
            return Retrofit.Builder()
                    .baseUrl(BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIService::class.java)
        }
    }

    @GET("everything")
    fun GetAllArticles(@QueryMap data:HashMap<String,String>): Call<APIData<Articles<Source>>>

    @GET("top-headlines")
    fun GetHeadLines(@QueryMap data:HashMap<String,String>): Call<APIData<Articles<Source>>>

    @GET("sources")
    fun GetSources(@QueryMap data:HashMap<String,String>): Call<APIDataList<Source>>

}
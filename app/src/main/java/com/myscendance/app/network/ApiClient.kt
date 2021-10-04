package com.myscendance.app.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    //base url
    private const val BASEURL = "https://app.myascendance.com/"

    //create a retrofit object
    private var retrofit: Retrofit? = null

    //initialize api service
    val apiService: ApiService = apiClient().create(ApiService::class.java)

    //initialize retrofit service
    private fun apiClient(): Retrofit {
        //build okhttp client
        val httpClient = OkHttpClient.Builder().readTimeout(100, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS).build()
        if (retrofit == null)
            retrofit = Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        //return the value
        return retrofit!!

    }

}
package com.example.hormigaahorradora.core.network
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://3f311d00defd4645bf72128d3a17d60e"
    // const val CLIENT_ID = ""

    private val loggin = HttpLoggingInterceptor().apply{
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor (loggin)
        .build()

    val OperationesApi : OperationesAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OperationesAPI::class.java)
    }

}
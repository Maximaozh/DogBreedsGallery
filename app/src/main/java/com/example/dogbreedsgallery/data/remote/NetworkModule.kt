package com.example.dogbreedsgallery.data.remote

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import com.example.dogbreedsgallery.BuildConfig

private val apiKeyInterceptor = Interceptor { chain ->
    val original = chain.request()
    val request = if (BuildConfig.DOG_API_KEY.isNotEmpty()) {
        original.newBuilder()
            .addHeader("x-api-key", BuildConfig.DOG_API_KEY)
            .build()
    } else {
        original
    }
    chain.proceed(request)
}


object NetworkModule {
    private const val BASE_URL = "https://api.thedogapi.com/v1/"
    private val moshi: Moshi = Moshi.Builder().build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
        }).build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val dogApiService: DogApiService by lazy {
        retrofit.create(DogApiService::class.java)
    }
}
package com.example.dogbreedsgallery

import android.content.Context
import com.example.dogbreedsgallery.data.local.DogDatabase
import com.example.dogbreedsgallery.data.remote.NetworkModule
import com.example.dogbreedsgallery.data.repository.BreedRepository

object AppDependencies {

    private lateinit var appContext: Context

    private val database: DogDatabase by lazy {
        DogDatabase.getInstance(appContext)
    }

    val breedDao by lazy { database.breedDao() }

    val breedRepository by lazy {
        BreedRepository(
            dao = breedDao,
            api = NetworkModule.dogApiService
        )
    }


    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun requireInited() {
        check(::appContext.isInitialized) {
            "AppDependencies.init(context) не был вызван при старте приложения"
        }
    }
}
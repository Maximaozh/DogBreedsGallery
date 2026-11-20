package com.example.dogbreedsgallery

import android.content.Context
import com.example.dogbreedsgallery.data.local.DogDatabase
import com.example.dogbreedsgallery.data.remote.NetworkModule
import com.example.dogbreedsgallery.data.repository.BreedRepository

object AppDependencies {

    private val database: DogDatabase by lazy {
        DogDatabase.getInstance(context)
    }

    private var context: Context? = null
        get() = field ?: throw IllegalStateException(
            "AppDependencies.init(context) не был вызван при старте приложения"
        )

    val breedDao by lazy { database.breedDao() }

    val breedRepository by lazy {
        BreedRepository(
            dao = breedDao,
            api = NetworkModule.dogApiService
        )
    }

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }
}
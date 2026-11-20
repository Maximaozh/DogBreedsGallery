package com.example.dogbreedsgallery.data.remote

import com.example.dogbreedsgallery.data.remote.dto.BreedDto
import com.example.dogbreedsgallery.data.remote.dto.BreedImageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DogApiService {

    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 1000
    ): List<BreedDto>

    @GET("images/search")
    suspend fun getBreedImages(
        @Query("breed_ids") breedIds: String,
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "RANDOM"
    ): List<BreedImageDto>

    @GET("breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String,
        @Query("limit") limit: Int = 100
    ): List<BreedDto>
}
package com.example.dogbreedsgallery.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKey {

    @Serializable
    data object BreedList : NavKey

    @Serializable
    data class BreedDetails(val breedId: Int) : NavKey

    @Serializable
    data object Favorites : NavKey
}
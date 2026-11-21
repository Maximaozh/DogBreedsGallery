package com.example.dogbreedsgallery.navigation

sealed interface NavKey : java.io.Serializable {
    data object BreedList : NavKey
    data class BreedDetails(val breedId: Int) : NavKey
    data object Favorites : NavKey
}
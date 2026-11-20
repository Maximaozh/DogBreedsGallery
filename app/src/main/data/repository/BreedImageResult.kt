package com.example.dogbreedsgallery.data.repository

data class BreedImageResult(
    val cache: List<com.example.dogbreedsgallery.data.local.BreedImageEntity>,
    val fresh: List<com.example.dogbreedsgallery.data.local.BreedImageEntity>? = null
)
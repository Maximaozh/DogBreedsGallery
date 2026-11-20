package com.example.dogbreedsgallery.data.remote.dto

import com.example.dogbreedsgallery.data.local.BreedEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreedDto(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "temperament")
    val temperament: String? = null,

    @Json(name = "origin")
    val origin: String? = null,

    @Json(name = "life_span")
    val lifeSpan: String? = null,

    @Json(name = "description")
    val description: String? = null,

    @Json(name = "image")
    val image: BreedImageDto? = null,

    @Json(name = "reference_image_id")
    val referenceImageId: String? = null,

    @Json(name = "bred_for")
    val bredFor: String? = null,

    @Json(name = "breed_group")
    val breedGroup: String? = null,

    @Json(name = "wikipedia_url")
    val wikipediaUrl: String? = null
) {
    fun toEntity(): BreedEntity {
        val imageUrl: String = image?.url
            ?: referenceImageId?.let { "https://cdn2.thedogapi.com/images/$it.jpg" }
            ?: ""
        return BreedEntity(
            id = id?.toIntOrNull() ?: (id?.hashCode() ?: 0),
            name = name ?: "Unknown breed",
            temperament = temperament ?: "",
            origin = origin,
            lifeSpan = lifeSpan ?: "",
            description = description,
            imageUrl = imageUrl,
            bredFor = bredFor,
            breedGroup = breedGroup,
            wikipediaUrl = wikipediaUrl,
            isFavorite = false
        )
    }
}
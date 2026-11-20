package com.example.dogbreedsgallery.data.remote.dto

import com.example.dogbreedsgallery.data.local.BreedImageEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreedImageDto(
    @Json(name = "id")
    val id: String? = null,

    @Json(name = "url")
    val url: String? = null,

    @Json(name = "width")
    val width: Int? = null,

    @Json(name = "height")
    val height: Int? = null
) {

    fun toEntity(breedId: Int): BreedImageEntity {
        return BreedImageEntity(
            id = id ?: url?.hashCode()?.toString() ?: "img_${breedId}_${hashCode()}",
            breedId = breedId,
            url = url ?: "",
            width = width ?: 0,
            height = height ?: 0
        )
    }
}
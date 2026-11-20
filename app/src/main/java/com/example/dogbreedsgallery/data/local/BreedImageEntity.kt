package com.example.dogbreedsgallery.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breed_images",
    indices = [Index("breedId")],
    foreignKeys = [
        ForeignKey(
            entity = BreedEntity::class,
            parentColumns = ["id"],
            childColumns = ["breedId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BreedImageEntity(
    @PrimaryKey
    val id: String,

    val breedId: Int,
    val url: String,
    val width: Int = 0,
    val height: Int = 0
)
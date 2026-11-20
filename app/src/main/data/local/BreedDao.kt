package com.example.dogbreedsgallery.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {
    @Query("SELECT * FROM breeds ORDER BY name ASC")
    fun observeBreeds(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavorites(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE id = :breedId")
    fun observeBreed(breedId: Int): Flow<BreedEntity?>

    @Query("SELECT * FROM breeds WHERE id = :breedId")
    suspend fun getBreed(breedId: Int): BreedEntity?

    @Upsert
    suspend fun upsertBreeds(breeds: List<BreedEntity>)

    @Query("UPDATE breeds SET isFavorite = :isFavorite WHERE id = :breedId")
    suspend fun setFavorite(breedId: Int, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM breeds")
    suspend fun countBreeds(): Int

    // Картинки
    @Query("SELECT * FROM breed_images WHERE breedId = :breedId ORDER BY id ASC")
    fun observeBreedImages(breedId: Int): Flow<List<BreedImageEntity>>

    @Upsert
    suspend fun upsertImages(images: List<BreedImageEntity>)

    @Query("DELETE FROM breed_images WHERE breedId = :breedId")
    suspend fun clearImagesForBreed(breedId: Int)
}
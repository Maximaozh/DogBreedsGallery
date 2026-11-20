package com.example.dogbreedsgallery.data.repository

import com.example.dogbreedsgallery.data.local.BreedDao
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.remote.DogApiService
import com.example.dogbreedsgallery.data.remote.dto.BreedImageDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BreedRepository(
    private val dao: BreedDao,
    private val api: DogApiService
) {
    fun observeBreeds(): Flow<List<BreedEntity>> = dao.observeBreeds()

    fun observeFavorites(): Flow<List<BreedEntity>> = dao.observeFavorites()

    fun observeBreed(breedId: Int): Flow<BreedEntity?> = dao.observeBreed(breedId)

    fun observeBreedImages(breedId: Int): Flow<List<com.example.dogbreedsgallery.data.local.BreedImageEntity>> =
        dao.observeBreedImages(breedId)

    suspend fun refreshBreeds(): Result<Int> {
        return try {
            val dtoList = api.getBreeds()

            val entities = dtoList.mapNotNull { dto ->
                val entity = dto.toEntity()
                if (entity.id == 0) return@mapNotNull null

                val local = dao.getBreed(entity.id)
                entity.copy(isFavorite = local?.isFavorite ?: false)
            }

            dao.upsertBreeds(entities)
            Result.Success(entities.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun refreshBreedImages(breedId: Int): Result<BreedImageResult> {
        return try {
            val dtoList: List<BreedImageDto> = api.getBreedImages(breedIds = breedId.toString())

            val fresh = dtoList.map { it.toEntity(breedId) }.filter { it.url.isNotEmpty() }
            dao.upsertImages(fresh)

            val cache = dao.observeBreedImages(breedId)
            Result.Success(BreedImageResult(cache = cache.first(), fresh = fresh))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun setFavorite(breedId: Int, isFavorite: Boolean) {
        dao.setFavorite(breedId, isFavorite)
    }

    suspend fun hasCachedBreeds(): Boolean {
        return dao.countBreeds() > 0
    }

    suspend fun getBreed(breedId: Int): BreedEntity? = dao.getBreed(breedId)
}
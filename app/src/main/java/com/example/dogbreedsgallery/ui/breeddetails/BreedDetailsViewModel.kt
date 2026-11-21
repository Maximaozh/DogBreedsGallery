package com.example.dogbreedsgallery.ui.breeddetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.local.BreedImageEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class BreedGalleryState {
    // Ни одного фото нет — покажем единственную заглушку из BreedEntity.imageUrl
    data object Empty : BreedGalleryState()
    // Есть хотя бы одно фото из Room
    data class Loaded(val images: List<BreedImageEntity>) : BreedGalleryState()
}

sealed class BreedDetailsUiState {
    data object Loading : BreedDetailsUiState()
    data class Success(val breed: BreedEntity) : BreedDetailsUiState()
    data object Error : BreedDetailsUiState()
}
class BreedDetailsViewModel(
    private val repository: BreedRepository,
    private val breedId: Int
) : ViewModel() {

    val uiState: StateFlow<BreedDetailsUiState> = repository.observeBreed(breedId)
        .map { breed ->
            if (breed != null) {
                BreedDetailsUiState.Success(breed)
            } else {
                BreedDetailsUiState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BreedDetailsUiState.Loading
        )

    val galleryState: StateFlow<BreedGalleryState> = repository.observeBreedImages(breedId)
        .map { images ->
            if (images.isEmpty()) {
                BreedGalleryState.Empty
            } else {
                BreedGalleryState.Loaded(images)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BreedGalleryState.Empty
        )

    init {
        refreshGallery()
    }

    fun refreshGallery() {
        viewModelScope.launch {
            repository.refreshBreedImages(breedId)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = repository.getBreed(breedId) ?: return@launch
            repository.setFavorite(breedId, !current.isFavorite)
        }
    }
}
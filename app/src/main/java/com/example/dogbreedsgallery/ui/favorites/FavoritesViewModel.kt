package com.example.dogbreedsgallery.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Success(val breeds: List<BreedEntity>) : FavoritesUiState()
    data object Empty : FavoritesUiState()
}

class FavoritesViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = repository.observeFavorites()
        .map { list ->
            if (list.isEmpty()) {
                FavoritesUiState.Empty
            } else {
                FavoritesUiState.Success(list)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState.Loading
        )

    fun removeFromFavorites(breed: BreedEntity) {
        viewModelScope.launch {
            repository.setFavorite(breed.id, isFavorite = false)
        }
    }
}
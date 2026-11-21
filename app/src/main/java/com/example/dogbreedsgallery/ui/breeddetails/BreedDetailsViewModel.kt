package com.example.dogbreedsgallery.ui.breeddetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = repository.getBreed(breedId) ?: return@launch
            repository.setFavorite(breedId, !current.isFavorite)
        }
    }
}
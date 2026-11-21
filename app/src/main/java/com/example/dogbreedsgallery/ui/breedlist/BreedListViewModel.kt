package com.example.dogbreedsgallery.ui.breedlist

// тут хотяб нормально импортов
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository
import com.example.dogbreedsgallery.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class BreedListUiState {
    data object Loading : BreedListUiState()
    data class Success(val breeds: List<BreedEntity>) : BreedListUiState()

    data object Empty : BreedListUiState()
    data class Error(val message: String) : BreedListUiState()
}

class BreedListViewModel(
    private val repository: BreedRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> = _searchQuery
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val uiState: StateFlow<BreedListUiState> = combine(
        repository.observeBreeds(),
        _searchQuery,
        _isRefreshing
    ) { breeds, query, isRefreshing ->

        if (breeds.isEmpty() && isRefreshing) {
            BreedListUiState.Loading
        } else {
            // Фильтрация по названию (без учёта регистра)
            val filtered = if (query.isBlank()) {
                breeds
            } else {
                breeds.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            when {
                filtered.isEmpty() && breeds.isNotEmpty() -> BreedListUiState.Empty
                filtered.isEmpty() -> BreedListUiState.Empty
                else -> BreedListUiState.Success(filtered)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BreedListUiState.Loading
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.refreshBreeds()
            _isRefreshing.value = false

            if (result is Result.Error && uiState.value is BreedListUiState.Loading) {
            }
        }
    }

    init {
        refresh()
    }
}
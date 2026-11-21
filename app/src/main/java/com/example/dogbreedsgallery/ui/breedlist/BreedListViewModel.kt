package com.example.dogbreedsgallery.ui.breedlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class BreedSortOrder {
    ALPHABETICAL,
    RANDOM
}

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

    private val _sortOrder = MutableStateFlow(BreedSortOrder.ALPHABETICAL)
    val sortOrder: StateFlow<BreedSortOrder> = _sortOrder

    private val _shuffleSeed = MutableStateFlow(0L)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val uiState: StateFlow<BreedListUiState> = combine(
        repository.observeBreeds(),
        _searchQuery,
        _sortOrder,
        _shuffleSeed,
        _isRefreshing
    ) { breeds, query, sort, seed, isRefreshing ->
        if (breeds.isEmpty() && isRefreshing) {
            BreedListUiState.Loading
        } else {
            val filtered = if (query.isBlank()) {
                breeds
            } else {
                breeds.filter { it.name.contains(query, ignoreCase = true) }
            }

            val result = when (sort) {
                BreedSortOrder.ALPHABETICAL ->
                    filtered.sortedBy { it.name.lowercase() }
                BreedSortOrder.RANDOM ->
                    filtered.shuffled(Random(seed))
            }

            if (result.isEmpty()) {
                BreedListUiState.Empty
            } else {
                BreedListUiState.Success(result)
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

    fun toggleSortOrder() {
        if (_sortOrder.value == BreedSortOrder.ALPHABETICAL) {
            _shuffleSeed.value = Random.nextLong()
            _sortOrder.value = BreedSortOrder.RANDOM
        } else {
            _sortOrder.value = BreedSortOrder.ALPHABETICAL
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            if (_sortOrder.value == BreedSortOrder.RANDOM) {
                _shuffleSeed.value = Random.nextLong()
            }

            repository.refreshBreeds()
            _isRefreshing.value = false
            onRefreshedToTop?.invoke()
        }
    }

    var onRefreshedToTop: (() -> Unit)? = null

    init {
        refresh()
    }
}
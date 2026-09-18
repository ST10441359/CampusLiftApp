package com.example.campuslift.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.dto.CreateTripRequest
import com.example.campuslift.Data.dto.TripWithAvailabilityDto
import com.example.campuslift.Data.dto.UpdateTripRequest
import com.example.campuslift.Data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    private val repo: TripRepository = TripRepository()
) : ViewModel() {

    private val _trips = MutableStateFlow<List<TripWithAvailabilityDto>>(emptyList())
    val trips: StateFlow<List<TripWithAvailabilityDto>> = _trips.asStateFlow()

    private val _selected = MutableStateFlow<TripWithAvailabilityDto?>(null)
    val selected: StateFlow<TripWithAvailabilityDto?> = _selected.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ---- SEARCH / LIST ----

    fun search(
        from: String? = null,
        to: String? = null,
        date: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.search(from, to, date, limit, offset)
                .onSuccess { _trips.value = it.items }
                .onFailure { _error.value = it.message ?: "Search failed" }
            _isLoading.value = false
        }
    }

    fun loadMine(limit: Int = 20, offset: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.mine(limit, offset)
                .onSuccess { _trips.value = it.items }
                .onFailure { _error.value = it.message ?: "Failed to load trips" }
            _isLoading.value = false
        }
    }

    fun loadTrip(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.get(id)
                .onSuccess { _selected.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load trip" }
            _isLoading.value = false
        }
    }

    // ---- MUTATIONS ----

    fun create(body: CreateTripRequest, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.create(body)
                .onSuccess {
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to create trip"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun update(
        id: String,
        body: UpdateTripRequest,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.update(id, body)
                .onSuccess {
                    _selected.value = it
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to update trip"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun cancel(id: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.cancel(id)
                .onSuccess {
                    // refresh whichever list is currently in scope
                    loadMine()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to cancel trip"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun complete(id: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.complete(id)
                .onSuccess {
                    loadMine()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to complete trip"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
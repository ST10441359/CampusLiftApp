package com.example.campuslift.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.dto.CreateVehicleRequest
import com.example.campuslift.Data.dto.UpdateVehicleRequest
import com.example.campuslift.Data.dto.VehicleDto
import com.example.campuslift.Data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val repo: VehicleRepository = VehicleRepository()
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<VehicleDto>>(emptyList())
    val vehicles: StateFlow<List<VehicleDto>> = _vehicles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.myVehicles()
                .onSuccess { _vehicles.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load vehicles" }
            _isLoading.value = false
        }
    }

    fun create(
        body: CreateVehicleRequest,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.create(body)
                .onSuccess {
                    loadVehicles()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to create vehicle"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun update(
        id: String,
        body: UpdateVehicleRequest,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.update(id, body)
                .onSuccess {
                    loadVehicles()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to update vehicle"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun delete(id: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.delete(id)
                .onSuccess {
                    loadVehicles()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to delete vehicle"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
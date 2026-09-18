package com.example.campuslift.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.dto.BookingWithTripDto
import com.example.campuslift.Data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val repo: BookingRepository = BookingRepository()
) : ViewModel() {

    // ---- Passenger: my bookings ----
    private val _myBookings = MutableStateFlow<List<BookingWithTripDto>>(emptyList())
    val myBookings: StateFlow<List<BookingWithTripDto>> = _myBookings.asStateFlow()

    // ---- Driver: bookings on one of my trips ----
    private val _tripBookings = MutableStateFlow<List<BookingWithTripDto>>(emptyList())
    val tripBookings: StateFlow<List<BookingWithTripDto>> = _tripBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ---------------- LOADS ----------------

    fun loadMyBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.myBookings()
                .onSuccess { _myBookings.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load bookings" }
            _isLoading.value = false
        }
    }

    fun loadForTrip(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.forTrip(tripId)
                .onSuccess { _tripBookings.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load bookings" }
            _isLoading.value = false
        }
    }

    // ---------------- MUTATIONS ----------------

    fun createBooking(
        tripId: String,
        seats: Int = 1,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.create(tripId, seats)
                .onSuccess { onDone(true) }
                .onFailure {
                    _error.value = it.message ?: "Failed to book"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun cancelBooking(bookingId: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.cancel(bookingId)
                .onSuccess {
                    // Refresh whichever list is populated
                    if (_myBookings.value.isNotEmpty()) loadMyBookings()
                    if (_tripBookings.value.isNotEmpty()) {
                        // Use trip id from the cancelled booking — we don't store it,
                        // so just let the caller decide via onDone(true) to re-fetch if needed.
                    }
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to cancel booking"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun approveBooking(
        bookingId: String,
        approve: Boolean,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.approve(bookingId, approve)
                .onSuccess { onDone(true) }
                .onFailure {
                    _error.value = it.message ?: "Failed to approve booking"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    fun confirmPickup(bookingId: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.confirmPickup(bookingId)
                .onSuccess {
                    loadMyBookings()
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to confirm pickup"
                    onDone(false)
                }
            _isLoading.value = false
        }
    }

    // ---------------- ERROR ----------------

    fun clearError() {
        _error.value = null
    }
}
package com.example.campuslift.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * CampusLift Settings ViewModel.
 * Bridges the Settings screen with the DataStore-based SettingsRepository.
 * Settings are PER-USER (keyed by Firebase UID).
 *
 * Author: Keshvir Parthab (ST10451537)
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "SettingsViewModel"

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    private val repository: SettingsRepository by lazy {
        Log.d(TAG, "Creating repository for user: $currentUserId")
        SettingsRepository(getApplication(), currentUserId)
    }

    // Toggles
    val darkMode: StateFlow<Boolean> = repository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = repository.biometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Text settings
    val language: StateFlow<String> = repository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val defaultPickup: StateFlow<String> = repository.defaultPickup
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Gateway")

    val emergencyContact: StateFlow<String> = repository.emergencyContact
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "+27 83 456 7890")

    val vehicle: StateFlow<String> = repository.vehicle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Toyota Etios")

    val paymentMethod: StateFlow<String> = repository.paymentMethod
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Standard Bank")

    // ---- Setters ----
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setBiometric(enabled: Boolean) {
        viewModelScope.launch { repository.setBiometric(enabled) }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.setNotifications(enabled) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { repository.setLanguage(lang) }
    }

    fun setDefaultPickup(value: String) {
        viewModelScope.launch { repository.setDefaultPickup(value) }
    }

    fun setEmergencyContact(value: String) {
        viewModelScope.launch { repository.setEmergencyContact(value) }
    }

    fun setVehicle(value: String) {
        viewModelScope.launch { repository.setVehicle(value) }
    }

    fun setPaymentMethod(value: String) {
        viewModelScope.launch { repository.setPaymentMethod(value) }
    }
}
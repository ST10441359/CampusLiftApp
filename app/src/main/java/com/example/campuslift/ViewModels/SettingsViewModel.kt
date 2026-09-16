package com.example.campuslift.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * CampusLift Settings ViewModel.
 * Bridges the Settings screen with the DataStore-based SettingsRepository.
 *
 * Author: Keshvir Parthab (ST10451537)
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private val TAG = "SettingsViewModel"

    // Expose settings as StateFlows so Compose can observe them
    val darkMode: StateFlow<Boolean> = repository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = repository.biometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language: StateFlow<String> = repository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    // --- Setters ---
    fun setDarkMode(enabled: Boolean) {
        Log.d(TAG, "setDarkMode: $enabled")
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setBiometric(enabled: Boolean) {
        Log.d(TAG, "setBiometric: $enabled")
        viewModelScope.launch { repository.setBiometric(enabled) }
    }

    fun setNotifications(enabled: Boolean) {
        Log.d(TAG, "setNotifications: $enabled")
        viewModelScope.launch { repository.setNotifications(enabled) }
    }

    fun setLanguage(lang: String) {
        Log.d(TAG, "setLanguage: $lang")
        viewModelScope.launch { repository.setLanguage(lang) }
    }
}
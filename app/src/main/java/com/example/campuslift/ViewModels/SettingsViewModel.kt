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

    // Get the current user's UID. If not signed in yet, fall back to "guest".
    // NOTE: FirebaseAuth.currentUser should be available by the time the app navigates
    // to Home (since SSO always precedes it), so this is usually correct.
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    private val repository: SettingsRepository by lazy {
        Log.d(TAG, "Creating repository for user: $currentUserId")
        SettingsRepository(getApplication(), currentUserId)
    }

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
}
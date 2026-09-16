package com.example.campuslift.Data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * CampusLift settings storage.
 * Saves user preferences to disk so they survive app restarts.
 *
 * Author: Keshvir Parthab (ST10451537)
 */

// Attach DataStore to the application context (singleton)
private val Context.dataStore by preferencesDataStore(name = "campuslift_settings")

class SettingsRepository(private val context: Context) {

    // Keys for each preference
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val BIOMETRIC = booleanPreferencesKey("biometric_enabled")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val LANGUAGE = stringPreferencesKey("language") // "en" or "zu"
    }

    // -------- Reads (Flows) --------
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DARK_MODE] ?: false }

    val biometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.BIOMETRIC] ?: true }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.NOTIFICATIONS] ?: true }

    val language: Flow<String> = context.dataStore.data
        .map { it[Keys.LANGUAGE] ?: "en" }

    // -------- Writes (suspend functions) --------
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setBiometric(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }
}
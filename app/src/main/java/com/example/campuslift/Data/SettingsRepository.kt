package com.example.campuslift.Data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * CampusLift settings storage.
 * Saves user preferences to disk per-user (keyed by Firebase UID).
 *
 * Author: Keshvir Parthab (ST10451537)
 */

// Attach DataStore to the application context (singleton)
private val Context.dataStore by preferencesDataStore(name = "campuslift_settings")

class SettingsRepository(private val context: Context, private val userKey: String) {

    private val TAG = "SettingsRepository"

    // Each user's keys are prefixed with their UID
    private fun key(name: String) = "${userKey}_$name"

    // -------- Reads (Flows) --------
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("dark_mode"))] ?: false }

    val biometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("biometric_enabled"))] ?: true }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("notifications_enabled"))] ?: true }

    val language: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("language"))] ?: "en" }

    // -------- Writes (suspend functions) --------
    suspend fun setDarkMode(enabled: Boolean) {
        Log.d(TAG, "[$userKey] setDarkMode: $enabled")
        context.dataStore.edit { it[booleanPreferencesKey(key("dark_mode"))] = enabled }
    }

    suspend fun setBiometric(enabled: Boolean) {
        Log.d(TAG, "[$userKey] setBiometric: $enabled")
        context.dataStore.edit { it[booleanPreferencesKey(key("biometric_enabled"))] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        Log.d(TAG, "[$userKey] setNotifications: $enabled")
        context.dataStore.edit { it[booleanPreferencesKey(key("notifications_enabled"))] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        Log.d(TAG, "[$userKey] setLanguage: $lang")
        context.dataStore.edit { it[stringPreferencesKey(key("language"))] = lang }
    }
}
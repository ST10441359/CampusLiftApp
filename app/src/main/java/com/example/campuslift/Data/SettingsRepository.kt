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

private val Context.dataStore by preferencesDataStore(name = "campuslift_settings")

class SettingsRepository(private val context: Context, private val userKey: String) {

    private val TAG = "SettingsRepository"

    private fun key(name: String) = "${userKey}_$name"

    // -------- Toggles --------
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("dark_mode"))] ?: false }

    val biometricEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("biometric_enabled"))] ?: true }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[booleanPreferencesKey(key("notifications_enabled"))] ?: true }

    // -------- Text settings --------
    val language: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("language"))] ?: "en" }

    val defaultPickup: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("default_pickup"))] ?: "Gateway" }

    val emergencyContact: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("emergency_contact"))] ?: "+27 83 456 7890" }

    val vehicle: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("vehicle"))] ?: "Toyota Etios" }

    val paymentMethod: Flow<String> = context.dataStore.data
        .map { it[stringPreferencesKey(key("payment_method"))] ?: "Standard Bank" }

    // -------- Setters (all suspend, called from ViewModel) --------
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

    suspend fun setDefaultPickup(value: String) {
        Log.d(TAG, "[$userKey] setDefaultPickup: $value")
        context.dataStore.edit { it[stringPreferencesKey(key("default_pickup"))] = value }
    }

    suspend fun setEmergencyContact(value: String) {
        Log.d(TAG, "[$userKey] setEmergencyContact: $value")
        context.dataStore.edit { it[stringPreferencesKey(key("emergency_contact"))] = value }
    }

    suspend fun setVehicle(value: String) {
        Log.d(TAG, "[$userKey] setVehicle: $value")
        context.dataStore.edit { it[stringPreferencesKey(key("vehicle"))] = value }
    }

    suspend fun setPaymentMethod(value: String) {
        Log.d(TAG, "[$userKey] setPaymentMethod: $value")
        context.dataStore.edit { it[stringPreferencesKey(key("payment_method"))] = value }
    }
}
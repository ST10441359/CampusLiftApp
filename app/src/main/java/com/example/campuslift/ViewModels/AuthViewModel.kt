package com.example.campuslift.ViewModels

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Auth.AuthRepository
import com.example.campuslift.Auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that mediates between the auth UI and the AuthRepository.
 * Handles validation, error messages, and loading state.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Mirror repository state into our StateFlow
        viewModelScope.launch {
            repository.authState.collect { state ->
                _authState.value = state
            }
        }
    }

    /** Sign in with validation and friendly error mapping. */
    fun signIn(email: String, password: String) {
        if (!validateEmailAndPassword(email, password)) return

        _isLoading.value = true
        _uiError.value = null

        viewModelScope.launch {
            Log.d(TAG, "signIn flow started")
            val result = repository.signInWithEmail(email.trim(), password)
            result.onFailure { e ->
                _uiError.value = mapAuthError(e.message)
                Log.e(TAG, "signIn error: ${_uiError.value}")
            }
            _isLoading.value = false
        }
    }

    /** Register with full validation. */
    /** Register with full validation. */
    /** Register with full validation. */
    fun register(name: String, surname: String, email: String, password: String, confirmPassword: String) {
        if (!validateEmailAndPassword(email, password)) return

        if (name.isBlank()) {
            _uiError.value = "First name is required"
            return
        }

        if (password != confirmPassword) {
            _uiError.value = "Passwords do not match"
            return
        }

        _isLoading.value = true
        _uiError.value = null

        viewModelScope.launch {
            Log.d(TAG, "register flow started")
            val result = repository.register(email.trim(), password)
            result.onSuccess {
                val displayName = "$name $surname".trim()
                val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    ?.updateProfile(profileUpdate)
            }
            result.onFailure { e ->
                _uiError.value = mapAuthError(e.message)
                Log.e(TAG, "register error: ${_uiError.value}")
            }
            _isLoading.value = false
        }
    }

    /** Google sign-in using the ID token from Credential Manager. */
    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        _uiError.value = null

        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)
            result.onFailure { e ->
                _uiError.value = "Google sign-in failed. Please try again."
                Log.e(TAG, "Google sign-in error", e)
            }
            _isLoading.value = false
        }
    }
    /** Complete Google Sign-In flow: Credential Manager → Firebase. */
    fun signInWithGoogle(context: android.content.Context) {
        _isLoading.value = true
        _uiError.value = null

        viewModelScope.launch {
            Log.d(TAG, "Google sign-in flow started")
            val helper = com.example.campuslift.Auth.GoogleSignInHelper(context)
            val tokenResult = helper.signIn()

            tokenResult.onSuccess { idToken ->
                Log.d(TAG, "Google token obtained, passing to Firebase")
                val result = repository.signInWithGoogle(idToken)
                result.onFailure { e ->
                    _uiError.value = "Google sign-in failed. Please try again."
                    Log.e(TAG, "Firebase Google sign-in error", e)
                }
            }.onFailure { e ->
                // User cancelled or error occurred
                val msg = e.message ?: ""
                _uiError.value = when {
                    msg.contains("cancel", true) -> null // Silent for user cancellation
                    msg.contains("no credentials", true) ->
                        "No Google accounts found on this device"
                    msg.contains("network", true) ->
                        "Network error. Check your connection"
                    else -> "Google sign-in failed. Please try again."
                }
                Log.e(TAG, "Google sign-in error: ${_uiError.value}")
            }

            _isLoading.value = false
        }
    }
    fun clearError() {
        _uiError.value = null
    }

    fun signOut() = repository.signOut()

    suspend fun getIdToken(): String? = repository.getIdToken()

    // ---- helpers ----

    private fun validateEmailAndPassword(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _uiError.value = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiError.value = "Please enter a valid email address"
            return false
        }
        if (password.isBlank()) {
            _uiError.value = "Password is required"
            return false
        }
        if (password.length < 6) {
            _uiError.value = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    /** Converts Firebase error strings into user-friendly messages. */
    private fun mapAuthError(raw: String?): String = when {
        raw == null -> "Something went wrong. Please try again."
        raw.contains("password is invalid", true) ||
                raw.contains("credential is incorrect", true) -> "Incorrect password"
        raw.contains("no user record", true) -> "No account found with that email"
        raw.contains("already in use", true) -> "An account already exists with this email"
        raw.contains("network", true) -> "Network error. Check your connection"
        raw.contains("badly formatted", true) -> "Please enter a valid email address"
        else -> "Authentication failed. Please try again."
    }
}
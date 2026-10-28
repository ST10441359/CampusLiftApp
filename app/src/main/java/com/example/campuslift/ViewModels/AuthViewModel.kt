package com.example.campuslift.ViewModels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Auth.AuthRepository
import com.example.campuslift.Auth.AuthState
import com.example.campuslift.Auth.GoogleSignInHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Mediates between the auth UI and AuthRepository.
 *
 * Note on Google sign-in context:
 *   GoogleSignInHelper MUST be constructed with an Activity context for the
 *   Credential Manager picker to render. We therefore accept a helper instance
 *   from the caller (the Compose screen, using LocalContext.current) rather than
 *   reconstructing one here from an application context.
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
        viewModelScope.launch {
            repository.authState.collect { state ->
                _authState.value = state
            }
        }
    }

    // ---- Email / password ----

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

    fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
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

    // ---- Google ----

    /**
     * Full Google SSO flow: Credential Manager → Firebase.
     *
     * The [helper] must be constructed with an Activity context by the caller.
     * Handles the sealed SignInResult:
     *   - Success   → pass idToken to Firebase
     *   - Cancelled → friendly snackbar, not an error
     *   - Failure   → map to a user-facing message
     */
    fun signInWithGoogle(helper: GoogleSignInHelper) {
        _isLoading.value = true
        _uiError.value = null

        viewModelScope.launch {
            Log.d(TAG, "Google sign-in flow started")
            when (val result = helper.signIn()) {
                is GoogleSignInHelper.SignInResult.Success -> {
                    Log.d(TAG, "Google token obtained, passing to Firebase")
                    val fbResult = repository.signInWithGoogle(result.idToken)
                    fbResult.onFailure { e ->
                        _uiError.value = "Google sign-in failed. Please try again."
                        Log.e(TAG, "Firebase Google sign-in error", e)
                    }
                }

                is GoogleSignInHelper.SignInResult.Cancelled -> {
                    // User dismissed the picker — show friendly message, no crash.
                    _uiError.value = "Sign-in cancelled"
                    Log.d(TAG, "User cancelled Google sign-in")
                }

                is GoogleSignInHelper.SignInResult.Failure -> {
                    _uiError.value = mapGoogleError(result.exception)
                    Log.e(TAG, "Google sign-in error", result.exception)
                }
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _uiError.value = null
    }

    // ---- Sign out ----

    /**
     * Signs out of Firebase AND clears Credential Manager state.
     *
     * The [helper] is the same instance passed to signInWithGoogle — reusing it
     * avoids reconstructing CredentialManager (cheap, but unnecessary) and keeps
     * the Activity context consistent.
     */
    fun signOut(helper: GoogleSignInHelper) {
        viewModelScope.launch {
            try {
                // 1. Clear Credential Manager state first.
                //    Doing this BEFORE Firebase signOut prevents the race where
                //    authState flips to Unauthenticated and triggers a re-login
                //    attempt while the stale grant is still cached.
                helper.clearCredentialState()

                // 2. Firebase signOut.
                repository.signOut()

                Log.d(TAG, "Successfully signed out and cleared credential state")
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign out", e)
                // Don't surface this to the user — sign-out is best-effort.
            }
        }
    }

    suspend fun getIdToken(): String? = repository.getIdToken()

    suspend fun getFreshIdToken(): String? = repository.getFreshIdToken()

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

    /**
     * Maps the Credential Manager failure to a user-facing message.
     * Cancellation is already handled separately; this only covers real errors.
     */
    private fun mapGoogleError(e: Throwable): String {
        val msg = e.message ?: ""
        return when {
            msg.contains("no credentials", true) ||
                    e is androidx.credentials.exceptions.NoCredentialException ->
                "No Google accounts found on this device"

            msg.contains("network", true) ->
                "Network error. Check your connection"

            msg.contains("developer", true) ||
                    msg.contains("10:", true) ->
                // Common misconfiguration: wrong web client ID or missing SHA-1
                "Google sign-in is misconfigured. Contact support."

            else -> "Google sign-in failed. Please try again."
        }
    }
}
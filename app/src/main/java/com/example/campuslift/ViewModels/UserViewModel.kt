package com.example.campuslift.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.dto.UserDto
import com.example.campuslift.Data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Called after Firebase login. Creates the Supabase user if missing,
     * otherwise returns the existing row.
     */
    fun syncAfterLogin(
        overrideName: String? = null,
        overrideSurname: String? = null,
        onDone: (Boolean) -> Unit = {}
    ) {
        val fb = FirebaseAuth.getInstance().currentUser
        if (fb == null) {
            _error.value = "No Firebase user signed in"
            onDone(false)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val firstName: String?
            val lastName: String?

            if (overrideName != null) {
                firstName = overrideName.takeIf { it.isNotBlank() }
                lastName = overrideSurname?.takeIf { it.isNotBlank() }
            } else {
                // Firebase doesn't expose first/last name directly — we split displayName
                val display = fb.displayName ?: ""
                val parts = display.split(" ", limit = 2)
                firstName = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
                lastName = parts.getOrNull(1)?.takeIf { it.isNotBlank() }
            }

            repo.sync(
                firebaseUid = fb.uid,
                email = fb.email,
                name = firstName,
                surname = lastName
            )
                .onSuccess {
                    _user.value = it
                    onDone(true)
                }
                .onFailure {
                    _error.value = it.message ?: "Sync failed"
                    onDone(false)
                }

            _isLoading.value = false
        }
    }

    /**
     * Loads /api/users/me — useful for a profile screen refresh.
     */
    fun loadMe() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.me()
                .onSuccess { _user.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load profile" }
            _isLoading.value = false
        }
    }
}